package io.dynamic.threadpool.server.event;

import org.springframework.util.StringUtils;

/**
 * Config Data Change Event.
 *
 * @author chen.ma
 * @date 2021/6/24 23:35
 */
public class ConfigDataChangeEvent extends Event {

    public final String namespace;

    public final String itemId;

    public final String tpId;

    public final long lastModifiedTs;

    public ConfigDataChangeEvent(String namespace, String itemId, String tpId, Long gmtModified) {
        if (StringUtils.isEmpty(namespace) || StringUtils.isEmpty(itemId) || StringUtils.isEmpty(tpId)) {
            throw new IllegalArgumentException("dataId is null or group is null");
        }

        this.namespace = namespace;
        this.itemId = itemId;
        this.tpId = tpId;
        this.lastModifiedTs = gmtModified;
    }
}
