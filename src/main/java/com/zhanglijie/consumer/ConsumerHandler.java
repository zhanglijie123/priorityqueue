package com.zhanglijie.consumer;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 14:01
 */
public interface ConsumerHandler {
    /**
     * 阻塞循环拉取消息，知道拉到消息为止
     * @param queueName 原始队列名
     * @return
     */
    public String pullBlocked(String queueName);

    /**
     * 轮训一次拉取消息（如果优先级队列暂时没数据就会返回null)
     * @param queueName
     * @return
     */
    public String pullOnce(String queueName);
}
