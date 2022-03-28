package cn.hippo4j.starter.core;

/**
 * Config service.
 *
 * @author chen.ma
 * @date 2021/6/21 21:49
 */
public interface ConfigService {

    /**
     * Add listener.
     *
     * @param tenantId
     * @param itemId
     * @param tpId
     * @param listener
     */
    void addListener(String tenantId, String itemId, String tpId, Listener listener);

    /**
     * Get server status.
     *
     * @return
     */
    String getServerStatus();

}
