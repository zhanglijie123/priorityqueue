package com.zhanglijie.entity;

import com.zhanglijie.core.client.HttpSqsClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 14:35
 */
@Data
@Slf4j
public class SqsClientManager {
    private static final String SEP_SERVER = ":";
    private static Map<String, HttpSqsClient> sqsClientMap = new HashMap<>();

    /**
     * 获得sqsClient
     * @param config
     * @return
     */
    public static HttpSqsClient getSqsClient(QueueConfigMessage config){
        String key = getKey(config);
        if(sqsClientMap.containsKey(key) && sqsClientMap.get(key)!=null){
            return sqsClientMap.get(key);
        }else{
            String queueServer = config.getQueueServer();
            String[] split = queueServer.split(SEP_SERVER);
            String ip = split[0];
            Integer port = Integer.valueOf(split[1]);
            HttpSqsClient httpSqsClient = new HttpSqsClient(ip, port);
            sqsClientMap.put(key,httpSqsClient);
            return httpSqsClient;
        }
    }

    public static HttpSqsClient removeAndReGetSqsClient(QueueConfigMessage config){
        String key = getKey(config);
        removeSqsClient(key);
        HttpSqsClient sqsClient = getSqsClient(config);
        log.info("this sqsClient has been replaced from cache,the key is {}",key);
        return sqsClient;
    }

    private static void removeSqsClient(String key) {
        sqsClientMap.remove(key);
        log.info("this sqsClient has been cleaned from cache,the key is {}",key);
    }

    private static String getKey(QueueConfigMessage config) {
        return config.getQueueServer();
    }
}
