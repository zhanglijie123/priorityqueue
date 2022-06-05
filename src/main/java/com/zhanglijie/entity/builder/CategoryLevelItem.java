package com.zhanglijie.entity.builder;

import com.zhanglijie.core.category.CategoryExecutor;
import com.zhanglijie.enu.QueueTypeEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 10:32
 */
@Data
@Builder
public class CategoryLevelItem {
   private CategoryExecutor categoryExecutor;
   private String targetQueue;
   private int level;
   private String queueServer;
   private String queueAuth;
   private QueueTypeEnum queueType;
   private String consumerGroup;

}
