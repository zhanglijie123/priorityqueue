package com.zhanglijie.producer;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 20:24
 */
public interface ProducerHandler {
    /**
     * 推送消息
     * @param content 消息内容
     * @param queue 目标队列
     */
    public void push(String content,String queue) throws InterruptedException;
}
