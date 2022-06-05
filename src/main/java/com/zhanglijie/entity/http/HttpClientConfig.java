package com.zhanglijie.entity.http;

import lombok.Data;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.ChallengeState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 15:44
 */
@Data
public class HttpClientConfig {
    private HttpHost proxyHost;
    private Credentials proxyUserInfo;
    private String postCharset = "utf-8";
    private int connectTimeOut = 3000;
    private int requestTimeOut = 30000;
    private int socketTimeOut = 10000;
    private List<Header> headers = new ArrayList<>();

    public RequestConfig getRequestConfig(){
        RequestConfig.Builder builder = RequestConfig.custom().setConnectTimeout(connectTimeOut).setConnectionRequestTimeout(requestTimeOut).setSocketTimeout(socketTimeOut);
        if(proxyHost!=null){
            builder.setProxy(proxyHost);
        }
        return builder.build();
    }

    public HttpContext getHttpContext(){
        HttpClientContext httpClientContext = new HttpClientContext();
        //代理认证
        if(proxyUserInfo != null){
            AuthState authState = new AuthState();
            authState.update(new BasicScheme(ChallengeState.PROXY),proxyUserInfo);
            httpClientContext.setAttribute(HttpClientContext.PROXY_AUTH_STATE,authState);
        }
        return httpClientContext;
    }

    public void setProxyHost(String host,int port,String protocol){proxyHost = new HttpHost(host,port,protocol);}

    public void setProxyUserInfo(String userName,String password){
        proxyUserInfo = new UsernamePasswordCredentials(userName,password);
    }
    public void addHeader(String name,String value){headers.add(new BasicHeader(name,value));}

    public final Header[] getHeaders(){return headers.toArray(new Header[headers.size()]);}
}
