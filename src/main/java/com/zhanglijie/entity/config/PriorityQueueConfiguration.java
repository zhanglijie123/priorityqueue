package com.zhanglijie.entity.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhanglijie
 * @version 1.0
 * @since 2022/3/7 17:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class PriorityQueueConfiguration {
    private Map<String,ConfigurationItem> configuration = new HashMap<>();
    private static volatile AtomicBoolean breakLock = new AtomicBoolean(false);
    public static boolean isBreak(){return breakLock.get();}

    public static void openBreakLock(){
        log.warn("priority queue`s break lock has been open,category task will not run");
        breakLock.compareAndSet(false,true);
    }
}
