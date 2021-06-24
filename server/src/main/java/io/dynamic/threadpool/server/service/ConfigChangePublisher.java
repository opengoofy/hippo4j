package io.dynamic.threadpool.server.service;

import io.dynamic.threadpool.server.event.ConfigDataChangeEvent;
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
     * @param event ConfigDataChangeEvent instance.
     */
    public static void notifyConfigChange(ConfigDataChangeEvent event) {
        NotifyCenter.publishEvent(event);
    }
}
