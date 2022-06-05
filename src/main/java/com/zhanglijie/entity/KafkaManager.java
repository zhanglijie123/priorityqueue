package com.zhanglijie.entity;

import cn.hutool.core.util.StrUtil;
import com.zhanglijie.entity.config.QueueConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 14:34
 */
@Slf4j
@Data
public class KafkaManager {
    private static final Integer sessionTimeout = 30000;
    private static final Integer pollMaxRecord  =1;
    private static final String OFFSET_POLICY = "earliest";
    private static final Integer heatbeatInterval = 5000;
    private static final Integer maxPollInterval = 90000;
    private static final String SEP = "_";
    private static Map<String, Consumer> consumerMap = new HashMap<>();
    private static Map<String, Producer> producerMap = new HashMap<>();

    /**
     * 根据配置信息创建消费者
     * @param config
     * @return
     */
    public static Consumer getConsumer(QueueConfigMessage config){
        String key = getConsumerKey(config);
        //直接从消费者连接池获得一个
        if(consumerMap.containsKey(key) && consumerMap.get(key) != null){
            return consumerMap.get(key);
        }else{
            Properties properties = generateConfig(config);
            Consumer<String, String> consumer = new KafkaConsumer<>(properties);
            consumer.subscribe(Arrays.asList(config.getQueueName()));
            consumerMap.put(key,consumer);
            log.info("create a new consumer success,the queue-server is {},the topic is {}",config.getQueueServer(),config.getQueueName());
            return consumer;
        }
    }

    private static String getConsumerKey(QueueConfigMessage config){
        String queueName = config.getQueueName();
        String consumerGroup = config.getConsumerGroup();
        return (queueName+SEP+consumerGroup+SEP+Thread.currentThread().getName());
    }

    /**
     * 创建消费端的配置
     * @param config
     * @return
     */
    private static Properties generateConfig(QueueConfigMessage config){
        Properties props = new Properties();
        //kafka集群地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getQueueServer());
        //消费组
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.getConsumerGroup());
        //自动提交偏移量
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        //最大会话时间，如果超过就会被踢出此消费组
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,sessionTimeout);
        //拉取间隔最大限制
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, pollMaxRecord);
        //key的反序列化方式
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //value的反序列化方式
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        //从最新的偏移量开始消费
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,OFFSET_POLICY);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,heatbeatInterval);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,maxPollInterval);

        //中台权限设置
        if(!StrUtil.isEmpty(config.getQueueAuth())){
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,"SASL_PLAINTEXT");
            props.put(SaslConfigs.SASL_MECHANISM,"SCRAM-SHA-256");
            props.put("sasl.jaas.config",config.getQueueAuth());
        }
        return props;
    }

    /**
     * 替换无效旧的消费者
     * @param config
     * @return
     */
    public static Consumer<String,String> removeAndReGetConsumer(QueueConfigMessage config){
        String key = getConsumerKey(config);
        removeConsumer(key);
        Consumer consumer = getConsumer(config);
        log.info("old consumer has been removed from cache,the key is {}",key);
        return consumer;
    }

    /**
     * 删除无效消费者
     * @param key
     */
    public static void removeConsumer(String key){
        consumerMap.remove(key);
        log.info("this consumer has been cleaned from cache ,the key is {}",key);
    }

    /**
     * 根据配置创建生产者
     * @param toQueueConfigMessage
     * @return
     */
    public static Producer<String,String> getProducer(QueueConfigMessage toQueueConfigMessage){
        String key = getProducerKey(toQueueConfigMessage);
        if(producerMap.containsKey(key)){
            return producerMap.get(key);
        }
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, toQueueConfigMessage.getQueueServer());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,  "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,  "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.ACKS_CONFIG,"all");
        //如果带了auth信息 一般指的是中台kafka
        if(!StrUtil.isEmpty(toQueueConfigMessage.getQueueAuth())){
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,  "SASL_PLAINTEXT");
            props.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
            props.put("sasl.jaas.config", toQueueConfigMessage.getQueueAuth());
        }

        Producer<String, String> producer = new KafkaProducer<>(props);
        log.info("create a new producer sucess,the queue-server is {}",toQueueConfigMessage.getQueueServer());
        producerMap.put(key,producer);
        return producer;
    }

    /**
     * 获得生产者key
     * @param toQueueConfigMessage
     * @return
     */
    private static String getProducerKey(QueueConfigMessage toQueueConfigMessage){
        String queueServer = toQueueConfigMessage.getQueueServer();
        String auth = toQueueConfigMessage.getQueueAuth();
        return (queueServer+SEP+auth+SEP+Thread.currentThread().getName());
    }

    public static  Producer<String,String> removeAndReGetProducer(QueueConfigMessage toQueueConfigMessage){
        String key= getProducerKey(toQueueConfigMessage);
        removeProducer(key);
        log.info("old producer has been removed from cache,the key is {}",key);
        return getProducer(toQueueConfigMessage);
    }

    /**
     * 移除生产者
     */
    public static void removeProducer(String key){
        producerMap.remove(key);
        log.info("this producer has been cleaned from cache, the key is {}",key);
    }
}

