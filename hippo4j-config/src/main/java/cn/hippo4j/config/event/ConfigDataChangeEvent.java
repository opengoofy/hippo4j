package cn.hippo4j.config.event;

import org.springframework.util.StringUtils;

/**
 * Config data change event.
 *
 * @author chen.ma
 * @date 2021/6/24 23:35
 */
public class ConfigDataChangeEvent extends AbstractEvent {

    public final String tenantId;

    public final String itemId;

    public final String tpId;

    public final long lastModifiedTs;

    public ConfigDataChangeEvent(String tenantId, String itemId, String tpId, Long gmtModified) {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(itemId) || StringUtils.isEmpty(tpId)) {
            throw new IllegalArgumentException("DataId is null or group is null");
        }

        this.tenantId = tenantId;
        this.itemId = itemId;
        this.tpId = tpId;
        this.lastModifiedTs = gmtModified;
    }

}
