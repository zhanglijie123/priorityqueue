package com.zhanglijie.entity.config;

import com.zhanglijie.enu.QueueTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 13:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueConfig {
    private String queueServer;
    private QueueTypeEnum queueType;
    private String queueAuth;
    private String consumerGroup;
}
