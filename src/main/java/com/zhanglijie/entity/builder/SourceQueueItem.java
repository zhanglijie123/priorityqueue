package com.zhanglijie.entity.builder;

import com.zhanglijie.enu.QueueTypeEnum;
import jdk.jfr.DataAmount;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 11:23
 */
@Data
@Builder
public class SourceQueueItem {
    private String sourceQueue;
    private String queueServer;
    private String queueAuth;
    private QueueTypeEnum queueType;
    private String consumerGroup;
}
