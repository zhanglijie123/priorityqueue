package com.zhanglijie.core.category.impl;

import com.zhanglijie.core.category.CategoryExecutor;
import com.zhanglijie.core.category.CategoryHandler;
import com.zhanglijie.entity.config.CategoryRule;
import com.zhanglijie.entity.config.RuleLevel;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 16:53
 */
@Slf4j
public class CategoryHandlerImpl implements CategoryHandler {
    private CategoryRule categoryRule;
    public CategoryHandlerImpl(CategoryRule categoryRule){this.categoryRule = categoryRule;}
    @Override
    public String handle(String content) {
        LinkedList<RuleLevel> ruleLevelList = categoryRule.getRuleLevelList();
        int size = ruleLevelList.size();
        int index = 0;
        for (RuleLevel ruleLevel : ruleLevelList) {
            if(index == (size-1)){
                log.info("the front level has not hint,it will be category to last level queue,the queue is {}",ruleLevel.getTargetQueue());
                return ruleLevel.getTargetQueue();
            }
            CategoryExecutor executor = ruleLevel.getCategoryClass();
            if(executor.hintQueueLevel(content))return ruleLevel.getTargetQueue();
            index++;
        }
        return null;
    }
}
