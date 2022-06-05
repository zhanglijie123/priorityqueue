package com.zhanglijie.producer.impl;

import com.zhanglijie.core.client.HttpSqsClient;
import com.zhanglijie.entity.SqsClientManager;
import com.zhanglijie.entity.config.RuleLevel;
import com.zhanglijie.producer.ProducerHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 20:25
 */
@Slf4j
public class SqsProducerHandler implements ProducerHandler {
    private RuleLevel ruleLevel;
    public SqsProducerHandler(RuleLevel ruleLevel){this.ruleLevel = ruleLevel;}
    @Override
    public void push(String content, String queue) throws InterruptedException {
        HttpSqsClient httpSqsClient = null;
        while(true){
            try{
                httpSqsClient = SqsClientManager.getSqsClient(ruleLevel.toQueueConfigMessage());
                httpSqsClient.put(queue,content);
                log.info("send success,the queue is {},the content is {}",queue,content);
            }catch (Exception e){
                log.info("send fail.the queue is {},the content is {},the exception msg is {}",queue,content,e.getMessage());
                Thread.sleep(3000);
                httpSqsClient = SqsClientManager.removeAndReGetSqsClient(ruleLevel.toQueueConfigMessage());
            }
        }
    }
}
