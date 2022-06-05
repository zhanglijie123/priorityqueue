package com.zhanglijie.enu;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 13:52
 */
public interface IEnum {

    /**
     * 获取编码
     *
     * @return 编码
     */
    @JsonValue
    String getCode();
}