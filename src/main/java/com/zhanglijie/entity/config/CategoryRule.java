package com.zhanglijie.entity.config;

import javafx.css.Rule;
import lombok.Data;

import java.util.LinkedList;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 13:44
 */
@Data
public class CategoryRule {
    private LinkedList<RuleLevel> ruleLevelList = new LinkedList<>();
    private RuleLevel head;
    private RuleLevel tail;
    public void addRuleLevel(RuleLevel ruleLevel){
        this.ruleLevelList.add(ruleLevel);
    }
    public CategoryRule(){};
    public CategoryRule(RuleLevel head,RuleLevel tail){
        this.head = head;
        this.tail = tail;
    }
}
