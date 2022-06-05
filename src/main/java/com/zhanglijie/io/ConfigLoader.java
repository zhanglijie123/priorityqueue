package com.zhanglijie.io;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhanglijie.core.category.CategoryExecutor;
import com.zhanglijie.entity.ConfigParamManager;
import com.zhanglijie.entity.builder.CategoryLevelItem;
import com.zhanglijie.entity.builder.ConfigurationBuilder;
import com.zhanglijie.entity.builder.SourceQueueItem;
import com.zhanglijie.entity.config.PriorityQueueConfiguration;
import com.zhanglijie.enu.QueueTypeEnum;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/8 10:26
 */
public class ConfigLoader {
    private static final char TRANSFER_CHAR = '-';

    private static List<Object> parse(String file) throws IOException {
        Object load = null;
        try(FileInputStream fis = new FileInputStream(new File(file))){
            Yaml yaml = new Yaml();
            load = yaml.load(fis);
            if(load == null){
                throw new IOException("load yml`s result is null");
            }
        }
        String s = JSONUtil.toJsonStr(trasfer(load));
        if(!JSONUtil.isJsonArray(s)){
            throw new IllegalArgumentException();
        }
        return JSONUtil.parseArray(s);
    }
    public static PriorityQueueConfiguration build(String file) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<Object> parse = parse(file);
        ConfigurationBuilder builder = ConfigurationBuilder.create();
        for (Object o : parse) {
            JSONObject jsonObject = (JSONObject)o;
            //封装queue-config
            String sourceQueue = (String)jsonObject.get(ConfigParamManager.getSourceQueue());
            JSONObject queueConfig = (JSONObject)jsonObject.get(ConfigParamManager.getQueueConfig());
            String queueType = (String)queueConfig.get(ConfigParamManager.getQueueType());
            String queueServer = (String)queueConfig.get(ConfigParamManager.getQueueServer());
            String queueAuth = (String)queueConfig.get(ConfigParamManager.getQueueAuth());
            String queueConsumerGroup = (String)queueConfig.get(ConfigParamManager.getConsumerGroup());
            SourceQueueItem queueItem = SourceQueueItem.builder().sourceQueue(sourceQueue).queueType(QueueTypeEnum.findByDesc(queueType)).queueServer(queueServer).queueAuth(queueAuth).consumerGroup(queueConsumerGroup).build();
            builder.addSoruceQueueItem(queueItem);

            //封装categoryRule
            List<JSONObject> categoryRules =(List<JSONObject>) jsonObject.get(ConfigParamManager.getCategoryRule());
            for (JSONObject categoryRule : categoryRules) {

                int level = (int)categoryRule.get(ConfigParamManager.getLEVEL());
                String targetQueue = (String)categoryRule.get(ConfigParamManager.getTargetQueue());
                String categoryClassStr = (String)categoryRule.get(ConfigParamManager.getCategoryClass());
                CategoryExecutor executor = null;
                if(!StrUtil.isEmpty(categoryClassStr)) {
                    executor =(CategoryExecutor) Class.forName(categoryClassStr).newInstance();
                }
                String queueAuthItem = null;
                String queueServerItem = null;
                QueueTypeEnum queueTypeEnum = null;
                String consumerGroupItem = null;
                if(categoryRule.containsKey(ConfigParamManager.getQueueConfig())){
                    JSONObject qconfig =(JSONObject) categoryRule.get(ConfigParamManager.getQueueConfig());
                    if(qconfig.containsKey(ConfigParamManager.getQueueAuth())){
                        queueAuthItem = (String) qconfig.get(ConfigParamManager.getQueueAuth());
                    }
                    if(qconfig.containsKey(ConfigParamManager.getConsumerGroup())){
                        consumerGroupItem = (String)qconfig.get(ConfigParamManager.getConsumerGroup());
                    }
                    queueTypeEnum = QueueTypeEnum.findByDesc((String)qconfig.get(ConfigParamManager.getQueueType()));
                    queueServerItem = (String)qconfig.get(ConfigParamManager.getQueueServer());
                }
                CategoryLevelItem categoryLevelItem = CategoryLevelItem.builder().categoryExecutor(executor).targetQueue(targetQueue).level(level).queueServer(queueServerItem).queueAuth(queueAuthItem).queueType(queueTypeEnum).consumerGroup(consumerGroupItem).build();
                builder.addCategoryLevelItem(sourceQueue,categoryLevelItem);
            }
        }

        return  builder.build();
    }

    private static Object trasfer(Object config) {
        if(config instanceof ArrayList){
            ArrayList<LinkedHashMap<String,Object>> newDatas = new ArrayList<>();
            for (LinkedHashMap<String, Object> item : (ArrayList<LinkedHashMap<String, Object>>) config) {
                newDatas.add((LinkedHashMap<String,Object>)trasfer(item));
            }
            return newDatas;
        }else if(config instanceof LinkedHashMap){
            LinkedHashMap<String, Object> newMap = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : ((LinkedHashMap<String, Object>) config).entrySet()) {
                String key = entry.getKey();
                while (true){
                    int index = key.indexOf(TRANSFER_CHAR);
                    if(index >= 0){
                        key = toUpper(key,index);
                    }else{
                        break;
                    }
                }
                //递归执行
                newMap.put(key,trasfer(entry.getValue()));
            }
            return newMap;
        }
        return config;
    }

    private static String toUpper(String v, int index) {
        char[] chars = v.toCharArray();
        char[] newChars = new char[chars.length - 1];
        //复制开始部分
        System.arraycopy(chars,0,newChars,0,index);
        //中间部分
        newChars[index] = (char)(((int)chars[index+1])-32);
        //复制结束部分
        System.arraycopy(chars,index+2,newChars,index+1,newChars.length-index-1);
        return new String(newChars);
    }
}
