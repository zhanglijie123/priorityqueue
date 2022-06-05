package com.zhanglijie.consumer.impl;

import cn.hutool.core.util.StrUtil;
import com.zhanglijie.consumer.ConsumerHandler;
import com.zhanglijie.entity.KafkaManager;
import com.zhanglijie.entity.config.CategoryRule;
import com.zhanglijie.entity.config.ConfigurationItem;
import com.zhanglijie.entity.config.RuleLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.LinkedList;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 14:12
 */
@Slf4j
public class KafkaConsumerServerHandler implements ConsumerHandler {
    private ConfigurationItem configurationItem;
    public KafkaConsumerServerHandler(ConfigurationItem configurationItem){this.configurationItem = configurationItem;}

    @Override
    public String pullBlocked(String originQueue) {
        CategoryRule categoryRule = configurationItem.getCategoryRule();
        LinkedList<RuleLevel> ruleLevelList = categoryRule.getRuleLevelList();
        String res = null;
        while(StrUtil.isEmpty(res)){
            res = doPoll(ruleLevelList,originQueue);
        }
        return res;
    }

    private String doPoll(LinkedList<RuleLevel> ruleLevelList, String originQueue) {
        ConsumerRecords<String,String> records = null;
        for (RuleLevel ruleLevel : ruleLevelList) {
            Consumer consumer = KafkaManager.getConsumer(ruleLevel.toQueueConfigMessage());
            try{
                records = consumer.poll(Duration.ofSeconds(2));
            }catch (Exception e){
                log.info("poll msg from {} happen exception,the exc msg is {},and the originQueue is {}",ruleLevel.getTargetQueue(),e.getMessage(),originQueue);
                consumer = KafkaManager.removeAndReGetConsumer(ruleLevel.toQueueConfigMessage());
            }
            if(records !=null && !records.isEmpty()){
                String res = null;
                for (ConsumerRecord<String, String> record : records) {
                    res = record.value();
                }
                log.info("poll msg from {} successfully,and the originQueue is {}, the content of msg is {}",ruleLevel.getTargetQueue(),originQueue,res);
                return res;
            }
        }
        return null;
    }

    @Override
    public String pullOnce(String originQueue) {
        CategoryRule categoryRule = configurationItem.getCategoryRule();
        LinkedList<RuleLevel> ruleLevelList = categoryRule.getRuleLevelList();
        ConsumerRecords<String,String> records = null;
        return doPoll(ruleLevelList,originQueue);
    }
}
