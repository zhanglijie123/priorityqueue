package com.zhanglijie.core.category;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 16:50
 */
public interface CategoryHandler {
    /**
     * 处理消息分类
     * @param content
     * @return
     */
    public String handle(String content);
}
