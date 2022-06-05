package com.zhanglijie.entity;

import com.zhanglijie.enu.QueueTypeEnum;
import lombok.Data;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 14:14
 */
@Data
public class QueueConfigMessage {
    private String queueServer;
    private QueueTypeEnum queueType;
    private String queueAuth;
    private String consumerGroup;
    private String queueName;
}
