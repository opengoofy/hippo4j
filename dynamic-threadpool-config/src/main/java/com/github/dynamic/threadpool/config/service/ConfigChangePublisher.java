package com.github.dynamic.threadpool.config.service;

import com.github.dynamic.threadpool.config.event.LocalDataChangeEvent;
import com.github.dynamic.threadpool.config.notify.NotifyCenter;

/**
 * Config change publisher.
 *
 * @author chen.ma
 * @date 2021/6/24 23:34
 */
public class ConfigChangePublisher {

    /**
     * Notify configChange.
     *
     * @param event
     */
    public static void notifyConfigChange(LocalDataChangeEvent event) {
        NotifyCenter.publishEvent(event);
    }

}
