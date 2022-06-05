package com.zhanglijie.core.check.impl;

import cn.hutool.core.util.StrUtil;
import com.zhanglijie.core.category.CategoryExecutor;
import com.zhanglijie.core.check.CheckParam;
import com.zhanglijie.entity.config.CategoryRule;
import com.zhanglijie.entity.config.ConfigurationItem;
import com.zhanglijie.entity.config.QueueConfig;
import com.zhanglijie.entity.config.RuleLevel;
import com.zhanglijie.enu.QueueTypeEnum;
import org.apache.kafka.common.protocol.types.Field;

import java.security.InvalidParameterException;
import java.util.LinkedList;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 16:57
 */
public class CheckConfParamImpl implements CheckParam {
    private static final String SEP = ":";

    @Override
    public void checkConfParam(ConfigurationItem configurationItem) {
        String sourceQueue = configurationItem.getSourceQueue();
        QueueConfig queueConfig = configurationItem.getQueueConfig();
        //检查source-queue是否为空
        if(StrUtil.isEmpty(sourceQueue)){
            throw new InvalidParameterException("source-queue must be configured");
        }
        //检查queue-config中所有元素
        checkQueueConfig(configurationItem.getQueueConfig());
        CategoryRule categoryRule = configurationItem.getCategoryRule();
        LinkedList<RuleLevel> ruleLevelList = categoryRule.getRuleLevelList();
        ruleLevelList.sort((a,b)->(b.getLevel()-a.getLevel()));
        int size = ruleLevelList.size();
        int index= 1;
        for (RuleLevel ruleLevel : ruleLevelList) {
            String targetQueue = ruleLevel.getTargetQueue();
            if(StrUtil.isEmpty(targetQueue)){
                throw new InvalidParameterException("target-queue must be configured");
            }
            if(index<size){
                CategoryExecutor categoryClass = ruleLevel.getCategoryClass();
                if(categoryClass == null){
                    throw new InvalidParameterException("category-class must be configured");
                }
            }
            if(ruleLevel.getQueueConfig() != null)checkQueueConfig(ruleLevel.getQueueConfig());
            index++;
        }
    }

    /**
     * 检测queue-config 标签是否合法
     * 检测点：
     *   1.queue-server 不能为空
     *   2.queue-type 不能为空
     *   3.queue-type 类型必须是kafka|sqs中其中一个
     *   4.如果是kafka队列，consumer-group配置不能为空
     * @param queueConfig
     */
    private void checkQueueConfig(QueueConfig queueConfig) {
        QueueTypeEnum queueType = queueConfig.getQueueType();
        String queueServer = queueConfig.getQueueServer();
        if(queueConfig == null || StrUtil.isEmpty(queueServer)){
            throw new InvalidParameterException("invalid queue-config in your configuration,it maybe that queue-server is empty!");
        }
        if(!queueServer.contains(SEP)){
            throw new InvalidParameterException("invalid queue-config in your configuration !!");
        }
        //检测queue-type
        if(queueType == null){
            throw new InvalidParameterException("queue-type can not be empty in your configuration");
        }
        //如果是kafka队列，consumer-group不能为空
        if(QueueTypeEnum.KAFKA == queueType){
            if(StrUtil.isEmpty(queueConfig.getConsumerGroup()))
            throw new InvalidParameterException("consumer-group can not be empty when kafka queue type !!");
        }
    }
}
