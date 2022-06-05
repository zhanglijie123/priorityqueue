package com.zhanglijie.core.client;

import cn.hutool.core.util.StrUtil;
import com.zhanglijie.entity.http.HttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 16:19
 */
@Slf4j
public class HttpSqsClient {
    private static final long SLEEP_TIME = 1000;
    private static final String OPTION_GET = "get";
    private static final String OPTION_PUT = "put";
    private static final String OPTION_STATUS = "status";
    private static final String PROTOCOL = "http";
    private static final int RIGHT_CODE = 200;

    /**
     * sqs关键字
     */
    private static final String HTTPSQS_GET_END = "HTTPSQS_GET_END";
    private static final String HTTPSQS_ERROR = "HTTPSQS_ERROR";
    private static final String HTTPSQS_PUT_OK = "HTTPSQS_PUT_OK";
    private String host;
    private int port;

    public HttpSqsClient(String host,int port){
        this.host = host;
        this.port = port;
    }

    /**
     * 异常类
     */
    public class HttpSqsException extends Exception{
        private String detailMessage;
        HttpSqsException(String message){this.detailMessage = message;}
    }

    /**
     * 消费
     * @param queue 队列名
     * @return
     */
    public String get(String queue) throws HttpSqsException {
        String url = joinUrl(queue, OPTION_STATUS);
        try{
           return getContent(HttpClient.doGet(url));
        }catch (Exception e){
            throw new HttpSqsException(e.getMessage());
        }
    }

    /**
     * 生产消息
     * @param queue
     * @return
     * @throws HttpSqsException
     */
    public boolean put(String queue, String body) throws HttpSqsException {
        String url = joinUrl(queue, OPTION_STATUS);
        try{
           String content = getContent(HttpClient.doPostTextPlain(url,body));
           return HTTPSQS_PUT_OK.equals(content);
        }catch (Exception e){
            throw new HttpSqsException(e.getMessage());
        }
    }
    /**
     * 拼接sqs连接并返回
     * @param queue
     * @param opt
     * @return
     */
    private String joinUrl(String queue,String opt){
        return String.format("%s://%s:%d?name=%s&opt=%s",PROTOCOL,host,port,queue,opt);
    }

    private String getContent(HttpResponse httpResponse){
        if(httpResponse.getStatusCode() != RIGHT_CODE){
            throw new RuntimeException("http code "+String.valueOf(httpResponse.getStatusCode()));
        }
        String content = httpResponse.toString();
        if(HTTPSQS_ERROR.equals(content)){
            throw new RuntimeException("httpsqs get error");
        }
        if(HTTPSQS_GET_END.equals(content)){
            return null;
        }else{
            return content;
        }
    }

    public Map getWithBlockBalance(List<String> queueNames, long sleepTime) throws HttpSqsException {
        if(sleepTime<0){
            sleepTime = SLEEP_TIME;
        }
        while(true){
            for(String queueName:queueNames){
                String sqsResult = get(queueName);
                if(!StrUtil.isEmpty(sqsResult)){
                    log.info("listen new data from {} success,the sqs result is {}",queueName,sqsResult);
                    HashMap<String, String> map = new HashMap<>();
                    map.put(queueName,sqsResult);
                    return map;
                }
            }
            try{
                Thread.sleep(sleepTime);
            }catch (Exception e){
                log.error("happen interruptedException,the exception msg is {}",e.getMessage());
            }
        }
    }
}
