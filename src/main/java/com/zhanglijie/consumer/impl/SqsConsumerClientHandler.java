package com.zhanglijie.consumer.impl;

import cn.hutool.core.util.StrUtil;
import com.zhanglijie.consumer.ConsumerHandler;
import com.zhanglijie.core.client.HttpSqsClient;
import com.zhanglijie.entity.SqsClientManager;
import com.zhanglijie.entity.config.CategoryRule;
import com.zhanglijie.entity.config.ConfigurationItem;
import com.zhanglijie.entity.config.RuleLevel;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 19:51
 */
@Slf4j
public class SqsConsumerClientHandler implements ConsumerHandler {

    private ConfigurationItem configurationItem;
    public SqsConsumerClientHandler(ConfigurationItem configurationItem){this.configurationItem = configurationItem;}

    @Override
    public String pullBlocked(String originQueue) {
        CategoryRule categoryRule = configurationItem.getCategoryRule();
        LinkedList<RuleLevel> ruleLevelList = categoryRule.getRuleLevelList();
        while(true){
            for (RuleLevel ruleLevel : ruleLevelList) {
                HttpSqsClient sqsClient = SqsClientManager.getSqsClient(ruleLevel.toQueueConfigMessage());
                try{
                    String res = sqsClient.get(ruleLevel.getTargetQueue());
                    if(!StrUtil.isEmpty(res)){
                       log.info("poll msg from {} successfully,and originQueue is {},the content of msg if {}",ruleLevel.getTargetQueue(),originQueue,res);
                       return res;
                    }
                }catch (Exception e){
                    log.info("poll msg from {} happen exception,the exc msg is {},and the originQueue is {}",ruleLevel.getTargetQueue(),e.getMessage(),originQueue);
                    sqsClient = SqsClientManager.removeAndReGetSqsClient(ruleLevel.toQueueConfigMessage());
                }
            }
        }
    }

    @Override
    public String pullOnce(String originQueue) {
        CategoryRule categoryRule = configurationItem.getCategoryRule();
        LinkedList<RuleLevel> ruleLevelList = categoryRule.getRuleLevelList();
            for (RuleLevel ruleLevel : ruleLevelList) {
                HttpSqsClient sqsClient = SqsClientManager.getSqsClient(ruleLevel.toQueueConfigMessage());
                try{
                    String res = sqsClient.get(ruleLevel.getTargetQueue());
                    if(!StrUtil.isEmpty(res)){
                        log.info("poll msg from {} successfully,and originQueue is {},the content of msg if {}",ruleLevel.getTargetQueue(),originQueue,res);
                        return res;
                    }
                }catch (Exception e){
                    log.info("poll msg from {} happen exception,the exc msg is {},and the originQueue is {}",ruleLevel.getTargetQueue(),e.getMessage(),originQueue);
                    sqsClient = SqsClientManager.removeAndReGetSqsClient(ruleLevel.toQueueConfigMessage());
                }
            }
            return null;
    }
}
