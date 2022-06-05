package com.zhanglijie.enu;


import com.zhanglijie.consumer.ConsumerHandler;
import com.zhanglijie.consumer.impl.KafkaConsumerClientHandler;
import com.zhanglijie.consumer.impl.KafkaConsumerServerHandler;
import com.zhanglijie.consumer.impl.SqsConsumerClientHandler;
import com.zhanglijie.consumer.impl.SqsConsumerServerHandler;
import com.zhanglijie.entity.config.ConfigurationItem;
import com.zhanglijie.entity.config.RuleLevel;
import com.zhanglijie.producer.ProducerHandler;
import com.zhanglijie.producer.impl.KafkaProducerHandler;
import com.zhanglijie.producer.impl.SqsProducerHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 13:59
 */
@Getter
@AllArgsConstructor
public enum QueueTypeEnum implements IEnum {
    /**
     * 描述
     */
    SQS("0", "sqs"),
    KAFKA("1","kafka")
    ;

    /**
     * 状态码
     */
    private String code;

    /**
     * 状态描述
     */
    private String description;

    /**
     * 根据编码查找枚举
     *
     * @param code 编码
     * @return {@link QueueTypeEnum } 实例
     **/
    public static QueueTypeEnum find(String code) {
        for (QueueTypeEnum instance : QueueTypeEnum.values()) {
            if (instance.getCode().equals(code)) {
                return instance;
            }
        }
        return null;
    }

    /**
     * 根据状态描述查找枚举
     *
     * @param description 编码
     * @return {@link QueueTypeEnum } 实例
     **/
    public static QueueTypeEnum findByDesc(String description) {
        for (QueueTypeEnum instance : QueueTypeEnum.values()) {
            if (instance.getDescription().equals(description)) {
                return instance;
            }
        }
        return null;
    }

    public static ConsumerHandler getConsumerHandler(QueueTypeEnum queueType, ConfigurationItem configurationItem,boolean isServer){
        if(isServer){
            if(QueueTypeEnum.KAFKA == queueType){
                return new KafkaConsumerServerHandler(configurationItem);
            }else{
                return new SqsConsumerServerHandler(configurationItem);
            }
        }else{
            if(QueueTypeEnum.KAFKA == queueType){
                return new KafkaConsumerClientHandler(configurationItem);
            }else{
                return new SqsConsumerClientHandler(configurationItem);
            }
        }
    }

    public static ProducerHandler getProducerHandler(QueueTypeEnum queueType, RuleLevel ruleLevel){
        if(QueueTypeEnum.KAFKA == queueType){
            return new KafkaProducerHandler(ruleLevel);
        }else{
            return new SqsProducerHandler(ruleLevel);
        }
    }

}