package cn.hippo4j.common.api;

import cn.hippo4j.common.notify.NotifyConfigDTO;

import java.util.List;
import java.util.Map;

/**
 * Notify config builder.
 *
 * @author chen.ma
 * @date 2022/2/24 19:50
 */
public interface NotifyConfigBuilder {

    /**
     * Build notify.
     *
     * @return
     */
    Map<String, List<NotifyConfigDTO>> buildNotify();

}
