package com.zhanglijie.core.category;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 16:51
 */
public interface CategoryExecutor {
    /**
     * 根据消息内容判断是否命中该等级队列
     * @param content
     * @return
     */
    public boolean hintQueueLevel(String content);
}
