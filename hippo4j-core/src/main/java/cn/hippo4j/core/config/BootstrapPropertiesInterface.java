package cn.hippo4j.core.config;

/**
 * Bootstrap properties interface.
 *
 * @author chen.ma
 * @date 2022/2/25 19:01
 */
public interface BootstrapPropertiesInterface {

    /**
     * Get enable.
     *
     * @return
     */
    Boolean getEnable();

    /**
     * Get username.
     *
     * @return
     */
    String getUsername();

    /**
     * Get password.
     *
     * @return
     */
    String getPassword();

    /**
     * Get namespace.
     *
     * @return
     */
    String getNamespace();

    /**
     * Get item id.
     *
     * @return
     */
    String getItemId();

    /**
     * Get server addr.
     *
     * @return
     */
    String getServerAddr();

}
