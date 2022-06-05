package com.zhanglijie.entity.http;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 15:54
 */
@Data
public class HttpResponse {
    private int statusCode;
    private Map<String, String> headers = new HashMap<>();
    private byte[] content;

    @Override
    public String toString() {
        return new String(content);
    }
}
