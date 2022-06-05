package com.zhanglijie.producer.impl;

import com.zhanglijie.entity.KafkaManager;
import com.zhanglijie.entity.config.RuleLevel;
import com.zhanglijie.producer.ProducerHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 20:25
 */
@Slf4j
public class KafkaProducerHandler implements ProducerHandler {
    private RuleLevel ruleLevel;
    public KafkaProducerHandler(RuleLevel ruleLevel){this.ruleLevel=ruleLevel;}
    @Override
    public void push(String content, String queue) throws InterruptedException {
        Producer<String,String> producer = null;
        while(true){
            try{
                producer = KafkaManager.getProducer(ruleLevel.toQueueConfigMessage());
                producer.send(new ProducerRecord<String,String>(queue,content)).get();
                log.info("send success,the queue is {},the content is {}",queue,content);
            }catch (Exception e){
                log.info("send fail.the queue is {},the content is {},the exception msg is {}",queue,content,e.getMessage());
                Thread.sleep(3000);
                producer = KafkaManager.removeAndReGetProducer(ruleLevel.toQueueConfigMessage());
            }
        }
    }
}
