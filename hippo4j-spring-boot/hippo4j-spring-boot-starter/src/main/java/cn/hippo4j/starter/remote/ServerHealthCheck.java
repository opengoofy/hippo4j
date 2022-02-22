package cn.hippo4j.starter.remote;

/**
 * Server health check.
 *
 * @author chen.ma
 * @date 2021/12/8 20:08
 */
public interface ServerHealthCheck {

    /**
     * Is health status.
     *
     * @return
     */
    boolean isHealthStatus();

    /**
     * Set health status.
     *
     * @param healthStatus
     */
    void setHealthStatus(boolean healthStatus);

}
