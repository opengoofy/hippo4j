package io.dynamic.threadpool.server.service;

import io.dynamic.threadpool.server.event.LocalDataChangeEvent;
import io.dynamic.threadpool.server.notify.NotifyCenter;

/**
 * Config Change Publisher.
 *
 * @author chen.ma
 * @date 2021/6/24 23:34
 */
public class ConfigChangePublisher {

    /**
     * Notify ConfigChange.
     *
     * @param event
     */
    public static void notifyConfigChange(LocalDataChangeEvent event) {
        NotifyCenter.publishEvent(event);
    }

}
