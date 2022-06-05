package com.zhanglijie.entity.config;

import com.zhanglijie.entity.QueueConfigMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 14:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationItem {
    private String sourceQueue;
    private QueueConfig queueConfig;
    private CategoryRule categoryRule;

    public QueueConfigMessage toQueueConfigMessage(){
        QueueConfigMessage queueConfig = new QueueConfigMessage();
        queueConfig.setConsumerGroup(this.queueConfig.getConsumerGroup());
        queueConfig.setQueueAuth(this.queueConfig.getQueueAuth());
        queueConfig.setQueueName(this.sourceQueue);
        queueConfig.setQueueServer(this.getQueueConfig().getQueueServer());
        queueConfig.setQueueType(this.getQueueConfig().getQueueType());
        return queueConfig;
    }
    public RuleLevel getRuleLevel(String targetQueue){
        LinkedList<RuleLevel> ruleLevelList = categoryRule.getRuleLevelList();
        for (RuleLevel ruleLevel : ruleLevelList) {
            if(ruleLevel.getTargetQueue().equals(targetQueue)){
                return ruleLevel;
            }
        }
        return null;
    }
}
