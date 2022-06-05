package com.zhanglijie.entity.builder;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.zhanglijie.core.check.impl.CheckConfParamImpl;
import com.zhanglijie.entity.config.*;
import jdk.jfr.Category;
import org.apache.kafka.common.utils.CollectionUtils;

import java.security.InvalidParameterException;
import java.sql.Struct;
import java.util.*;
import java.util.zip.Checksum;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 10:32
 */
public class ConfigurationBuilder {
    private Map<String, List<CategoryLevelItem>> categoryLevelItems;
    private List<SourceQueueItem> sourceQueueItems;
    public static ConfigurationBuilder create(){return new ConfigurationBuilder();}
    protected ConfigurationBuilder(){super();}

    public final ConfigurationBuilder addCategoryLevelItem(final String sourceQueue,final CategoryLevelItem categoryLevelItem){
        if(categoryLevelItem == null){
            return this;
        }
        if(categoryLevelItems == null){
            categoryLevelItems = new HashMap<>();
        }
        if(categoryLevelItems.containsKey(sourceQueue)){
            List<CategoryLevelItem> list = this.categoryLevelItems.get(sourceQueue);
            list.add(categoryLevelItem);
            categoryLevelItems.put(sourceQueue,list);
        }else{
            ArrayList<CategoryLevelItem> list = new ArrayList<>();
            list.add(categoryLevelItem);
            this.categoryLevelItems.put(sourceQueue,list);
        }
        return this;
    }
    public final ConfigurationBuilder addSoruceQueueItem(final SourceQueueItem sourceQueueItem){
        if(sourceQueueItem == null){
            return this;
        }
        if(sourceQueueItems == null){
            sourceQueueItems = new ArrayList<>();
        }
        sourceQueueItems.add(sourceQueueItem);
        return this;
    }
    public PriorityQueueConfiguration build(){
        PriorityQueueConfiguration configuration = new PriorityQueueConfiguration();
        List<ConfigurationItem> configurationItems = transfer();
        if(CollectionUtil.isEmpty(configurationItems)){
            throw new InvalidParameterException("config fail maybe lost of configuring source-queue or categoru-rule !!");
        }
        for (ConfigurationItem configurationItem : configurationItems) {
            //check
            CheckConfParamImpl check = new CheckConfParamImpl();
            check.checkConfParam(configurationItem);
            //优化-》比如给默认值 ，比如数据冗余
            String sourceQueue = configurationItem.getSourceQueue();
            QueueConfig queueConfig = configurationItem.getQueueConfig();
            LinkedList<RuleLevel> ruleLevelList = configurationItem.getCategoryRule().getRuleLevelList();
            //继承使用父亲节点信息-queue-config
            for (RuleLevel ruleLevel : ruleLevelList) {
                QueueConfig childQueueConfig = ruleLevel.getQueueConfig();
                if(childQueueConfig == null){
                    ruleLevel.setQueueConfig(queueConfig);
                }
            }
            configurationItem.getCategoryRule().setHead(ruleLevelList.peekFirst());
            configurationItem.getCategoryRule().setTail(ruleLevelList.peekLast());
            configuration.getConfiguration().put(sourceQueue,configurationItem);
        }
        return configuration;
    }

    private List<ConfigurationItem> transfer() {
        ArrayList<ConfigurationItem> configurationItems = new ArrayList<>();
        for (SourceQueueItem sourceQueueItem : sourceQueueItems) {
            String sourceQueue = sourceQueueItem.getSourceQueue();
            if(StrUtil.isEmpty(sourceQueue))continue;
            if((!categoryLevelItems.containsKey(sourceQueue)) || CollectionUtil.isEmpty(categoryLevelItems.get(sourceQueue)))continue;

            //封装原始队列信息
            ConfigurationItem configurationItem = new ConfigurationItem();
            configurationItem.setSourceQueue(sourceQueue);
            QueueConfig sourceQueueConfig = QueueConfig.builder().queueServer(sourceQueueItem.getQueueServer()).queueAuth(sourceQueueItem.getQueueAuth()).consumerGroup(sourceQueueItem.getConsumerGroup()).queueType(sourceQueueItem.getQueueType()).build();
            configurationItem.setQueueConfig(sourceQueueConfig);

            CategoryRule categoryRule = new CategoryRule();
            //封装分类等级队列信息
            for (CategoryLevelItem categoryLevelItem : categoryLevelItems.get(sourceQueue)) {
                String queueServer = categoryLevelItem.getQueueServer();
                RuleLevel ruleLevel = new RuleLevel();
                if(!StrUtil.isEmpty(queueServer)){
                    QueueConfig build = QueueConfig.builder()
                            .queueServer(queueServer)
                            .queueType(categoryLevelItem.getQueueType())
                            .queueAuth(categoryLevelItem.getQueueAuth())
                            .consumerGroup(categoryLevelItem.getConsumerGroup())
                            .build();
                    ruleLevel.setQueueConfig(build);
                }
                ruleLevel.setTargetQueue(categoryLevelItem.getTargetQueue());
                ruleLevel.setCategoryClass(categoryLevelItem.getCategoryExecutor());
                ruleLevel.setLevel(categoryLevelItem.getLevel());
                categoryRule.addRuleLevel(ruleLevel);
            }
            configurationItem.setCategoryRule(categoryRule);
            configurationItems.add(configurationItem);
        }
        return configurationItems;
    }
}
