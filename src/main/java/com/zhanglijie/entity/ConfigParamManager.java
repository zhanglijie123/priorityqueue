package com.zhanglijie.entity;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 14:30
 */
public class ConfigParamManager {
    private static final String SOURCE_QUEUE = "sourceQueue";
    private static final String QUEUE_TYPE = "queueType";
    private static final String QUEUE_CONFIG = "queueConfig";
    private static final String QUEUE_SERVER = "queueServer";
    private static final String QUEUE_AUTH = "queueAuth";
    private static final String CATEGORY_RULE = "categoryRule";
    private static final String LEVEL = "level";
    private static final String CATEGORY_CLASS = "categoryClass";
    private static final String TARGET_QUEUE = "targetQueue";
    private static final String CONSUMER_GROUP = "consumerGroup";

    public static String getSourceQueue() {
        return SOURCE_QUEUE;
    }

    public static String getQueueType() {
        return QUEUE_TYPE;
    }

    public static String getQueueConfig() {
        return QUEUE_CONFIG;
    }

    public static String getQueueServer() {
        return QUEUE_SERVER;
    }

    public static String getQueueAuth() {
        return QUEUE_AUTH;
    }

    public static String getCategoryRule() {
        return CATEGORY_RULE;
    }

    public static String getLEVEL() {
        return LEVEL;
    }

    public static String getCategoryClass() {
        return CATEGORY_CLASS;
    }

    public static String getTargetQueue() {
        return TARGET_QUEUE;
    }

    public static String getConsumerGroup() {
        return CONSUMER_GROUP;
    }
}
