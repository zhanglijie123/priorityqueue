package com.zhanglijie.core.client;

import com.zhanglijie.entity.http.HttpClientConfig;
import com.zhanglijie.entity.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 15:41
 */
public class HttpClient {
    private static CloseableHttpClient httpClient;
    static{
        httpClient = HttpClientBuilder.create().setMaxConnPerRoute(10).setMaxConnTotal(50).build();
    }

    public static HttpResponse doGet(String url) throws IOException, URISyntaxException {
        return HttpClient.doGet(url,new HttpClientConfig());
    }

    public static HttpResponse doGet(String url, HttpClientConfig config) throws URISyntaxException, IOException {
        HttpGet get = new HttpGet(new URIBuilder(url).build());
        get.setHeaders(config.getHeaders());
        //配置
        get.setConfig(config.getRequestConfig());
        return processor(get,config.getHttpContext());
    }

    private static HttpResponse processor(HttpUriRequest request, HttpContext httpContext) throws IOException {
        CloseableHttpResponse execute = httpClient.execute(request, httpContext);
        HttpEntity entity = execute.getEntity();
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(execute.getStatusLine().getStatusCode());
        httpResponse.setContent(EntityUtils.toByteArray(entity));
        return httpResponse;

    }

    public static HttpResponse doPostTextPlain(String url,String param) throws IOException, URISyntaxException {
        return HttpClient.doPostTextPlain(url,param,new HttpClientConfig());
    }

    public static HttpResponse doPostTextPlain(String url,String param,HttpClientConfig config) throws URISyntaxException, IOException {
        HttpPost post = new HttpPost(new URIBuilder(url).build());
        post.setHeaders(config.getHeaders());
        //配置
        post.setConfig(config.getRequestConfig());
        post.setEntity(new StringEntity(param,config.getPostCharset()));
        return processor(post,config.getHttpContext());
    }
}
