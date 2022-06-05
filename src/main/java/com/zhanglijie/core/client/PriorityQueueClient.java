package com.zhanglijie.core.client;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 16:44
 */
public interface PriorityQueueClient {


    /**
     * 阻塞拉取消息队列数据
     * @param originQueue 原始队列
     * @return 优先级队列的数据
     */
    public String pullBlocked(String originQueue);


    /**
     * 只轮训一次拉取消息队列数据
     * @param originQueue 原始队列
     * @return 优先级队列的数据
     */
    public String pullOnce(String originQueue);
}
