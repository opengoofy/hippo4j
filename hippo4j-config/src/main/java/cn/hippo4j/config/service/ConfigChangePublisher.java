package cn.hippo4j.config.service;

import cn.hippo4j.config.notify.NotifyCenter;
import cn.hippo4j.config.event.LocalDataChangeEvent;

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
