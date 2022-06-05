package com.zhanglijie.core.check;

import com.zhanglijie.entity.config.ConfigurationItem;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 16:56
 */
public interface CheckParam {
    /**
     * 检查配置参数是否合法
     * @param configurationItem
     */
    public void checkConfParam(ConfigurationItem configurationItem);
}
