package com.zhanglijie;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.zhanglijie.consumer.ConsumerHandler;
import com.zhanglijie.core.TaskRunner;
import com.zhanglijie.core.client.PriorityQueueClient;
import com.zhanglijie.core.impl.TaskRunnerImpl;
import com.zhanglijie.entity.config.ConfigurationItem;
import com.zhanglijie.entity.config.PriorityQueueConfiguration;
import com.zhanglijie.enu.QueueTypeEnum;

import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/8 10:02
 */
public class Engine {
    private static ThreadPoolExecutor threadPoolExecutor = null;
    public static PriorityQueueClient start(PriorityQueueConfiguration configuration){
        //根据configuration配置项数量决定线程参数大小
        int num = configuration.getConfiguration().size();
        threadPoolExecutor = new ThreadPoolExecutor(num,num,0, TimeUnit.SECONDS,new LinkedBlockingDeque<>(),new ThreadFactoryBuilder().setNamePrefix("priority-queue-thread-").build());
        PriorityQueueClient priorityQueueClient = new PriorityQueueClient(){

            @Override
            public String pullBlocked(String originQueue) {
                if(!checkIsStarted(configuration) || !configuration.getConfiguration().containsKey(originQueue)){
                    throw new RuntimeException("engine had not started up successfully");
                }
                return doBlockedPull(originQueue,configuration);
            }

            @Override
            public String pullOnce(String originQueue) {
                if(!checkIsStarted(configuration) || !configuration.getConfiguration().containsKey(originQueue)){
                    throw new RuntimeException("engine had not started up successfully");
                }
                return doPullOnce(originQueue,configuration);
            }
        };
        for (Map.Entry<String, ConfigurationItem> entry : configuration.getConfiguration().entrySet()) {
            TaskRunnerImpl taskRunner = new TaskRunnerImpl(entry.getValue());
            threadPoolExecutor.submit(()->taskRunner.run());
        }
        return priorityQueueClient;
    }

    /**
     * 轮训只拉一遍
     * @param originQueue
     * @param configuration
     * @return
     */
    private static String doPullOnce(String originQueue, PriorityQueueConfiguration configuration) {
        Map<String, ConfigurationItem> conf = configuration.getConfiguration();
        if(!conf.containsKey(originQueue)){
            throw new RuntimeException("can not find "+originQueue+"in your config");
        }
        ConfigurationItem configurationItem = conf.get(originQueue);
        ConsumerHandler consumerHandler = QueueTypeEnum.getConsumerHandler(configurationItem.getQueueConfig().getQueueType(), configurationItem, false);
        return consumerHandler.pullOnce(originQueue);

    }

    /**
     * 阻塞拉
     * @param originQueue
     * @param configuration
     * @return
     */
    private static String doBlockedPull(String originQueue, PriorityQueueConfiguration configuration) {
        Map<String, ConfigurationItem> config = configuration.getConfiguration();
        if(!config.containsKey(originQueue)){
            throw new RuntimeException("can not find "+originQueue+"in your config");
        }
        ConfigurationItem configurationItem = config.get(originQueue);
        ConsumerHandler consumerHandler = QueueTypeEnum.getConsumerHandler(configurationItem.getQueueConfig().getQueueType(), configurationItem, false);
        return consumerHandler.pullBlocked(originQueue);
    }

    private static boolean checkIsStarted(PriorityQueueConfiguration configuration) {
        //没有启动起来
        if(configuration == null || CollectionUtil.isEmpty(configuration.getConfiguration())){
            return false;
        }
        //正常启动
        return true;
    }
}
