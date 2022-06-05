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

        import java.sql.Struct;
        import java.time.Duration;
        import java.util.LinkedList;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 14:12
 */
@Slf4j
public class KafkaConsumerClientHandler implements ConsumerHandler {
    private ConfigurationItem configurationItem;

    public KafkaConsumerClientHandler(ConfigurationItem configurationItem) {
        this.configurationItem = configurationItem;
    }

    @Override
    public String pullBlocked(String originQueue) {
        ConsumerRecords<String, String> records = null;
        while (records == null || records.isEmpty()) {
            Consumer consumer = KafkaManager.getConsumer(configurationItem.toQueueConfigMessage());
            try {
                records = consumer.poll(Duration.ofSeconds(1));
            } catch (Exception e) {
                log.info("poll msg from {} happen exception,the exc msg is {}", configurationItem.getSourceQueue(), e.getMessage());
                consumer = KafkaManager.removeAndReGetConsumer(configurationItem.toQueueConfigMessage());
            }
        }
        return getResult(records);
    }

    private String getResult(ConsumerRecords<String, String> records) {
        String res = null;
        if(records != null && !records.isEmpty()){
            for (ConsumerRecord<String, String> record : records) {
                res = record.value();
            }
            log.info("poll msg from {} successfully,the content of msg is {}",configurationItem.getSourceQueue(),res);
        }
        return res;
    }


    @Override
    public String pullOnce(String originQueue) {
        ConsumerRecords<String, String> records = null;
        Consumer consumer = KafkaManager.getConsumer(configurationItem.toQueueConfigMessage());
        try {
            records = consumer.poll(Duration.ofSeconds(1));
        } catch (Exception e) {
            log.info("poll msg from {} happen exception,the exc msg is {}", configurationItem.getSourceQueue(), e.getMessage());
            consumer = KafkaManager.getConsumer(configurationItem.toQueueConfigMessage());
        }
        return getResult(records);
    }
}
