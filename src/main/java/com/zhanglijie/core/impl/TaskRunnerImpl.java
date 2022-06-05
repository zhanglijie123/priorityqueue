package com.zhanglijie.core.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.zhanglijie.consumer.ConsumerHandler;
import com.zhanglijie.core.TaskRunner;
import com.zhanglijie.core.category.impl.CategoryHandlerImpl;
import com.zhanglijie.entity.config.ConfigurationItem;
import com.zhanglijie.entity.config.PriorityQueueConfiguration;
import com.zhanglijie.entity.config.QueueConfig;
import com.zhanglijie.entity.config.RuleLevel;
import com.zhanglijie.enu.QueueTypeEnum;
import com.zhanglijie.producer.ProducerHandler;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.processing.SupportedAnnotationTypes;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 17:03
 */
@Slf4j
public class TaskRunnerImpl implements TaskRunner {
    private ConfigurationItem configurationItem;
    public TaskRunnerImpl(ConfigurationItem configurationItem){this.configurationItem=configurationItem;}

    @Override
    public void run(){
       if(PriorityQueueConfiguration.isBreak()){
          log.info("ban on running because of break-resume ...");
       }
       while(!PriorityQueueConfiguration.isBreak()){
           String content = null;
           try{
               //拉取原始队列消息
               QueueConfig queueConfig = configurationItem.getQueueConfig();
               ConsumerHandler consumerHandler = QueueTypeEnum.getConsumerHandler(queueConfig.getQueueType(), configurationItem, true);
               content = consumerHandler.pullBlocked(configurationItem.getSourceQueue());
               if(StrUtil.isEmpty(content))continue;

               //分类处理
               String targetQueue = null;
               try{
                   CategoryHandlerImpl categoryHandler = new CategoryHandlerImpl(configurationItem.getCategoryRule());
                   targetQueue = categoryHandler.handle(content);
               }catch (Exception e){
                   log.error("category msg happen exception,the exc msg is {},the msg content is {},the originQueue is {}",e.getMessage(),content,configurationItem.getSourceQueue());
                   if(!StrUtil.isEmpty(content)){
                       //推送优先级最低的队列
                       RuleLevel tail = configurationItem.getCategoryRule().getTail();
                       ProducerHandler producerHandler = QueueTypeEnum.getProducerHandler(tail.getQueueConfig().getQueueType(), tail);
                       try{
                           producerHandler.push(content,tail.getTargetQueue());
                       }catch (Exception exc){
                           log.info("re-push happen exception,the exc msg is {}",exc.getMessage());
                       }
                   }
               }
               //推送优先级队列
               RuleLevel ruleLevel = configurationItem.getRuleLevel(targetQueue);
               ProducerHandler producerHandler = QueueTypeEnum.getProducerHandler(ruleLevel.getQueueConfig().getQueueType(), ruleLevel);
               producerHandler.push(content,targetQueue);
           }catch (Exception ex){
               log.error("runner happen exception,the exc msg is {},the msg content is {},the origin queue is {}",ex.getMessage(),content,configurationItem.getSourceQueue());
           }
       }
    }
}
