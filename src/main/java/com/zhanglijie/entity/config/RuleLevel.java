package com.zhanglijie.entity.config;

import com.zhanglijie.core.category.CategoryExecutor;
import com.zhanglijie.entity.QueueConfigMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 13:45
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleLevel {
    private int level;
    private CategoryExecutor categoryClass;
    private String targetQueue;
    private QueueConfig queueConfig;
    public void setCategoryClass(String categoryClass) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> aClass = Class.forName(categoryClass);
        CategoryExecutor executor = (CategoryExecutor) aClass.newInstance();
        this.categoryClass = executor;
    }

    public void setCategoryClass(CategoryExecutor categoryClass){
        this.categoryClass = categoryClass;
    }

    public QueueConfigMessage toQueueConfigMessage(){
        QueueConfigMessage queueConfig = new QueueConfigMessage();
        queueConfig.setConsumerGroup(this.queueConfig.getConsumerGroup());
        queueConfig.setQueueAuth(this.queueConfig.getQueueAuth());
        queueConfig.setQueueName(this.targetQueue);
        queueConfig.setQueueServer(this.getQueueConfig().getQueueServer());
        queueConfig.setQueueType(this.getQueueConfig().getQueueType());
        return queueConfig;
    }
}
