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
    default Boolean getEnable() {
        return null;
    }

    /**
     * Get username.
     *
     * @return
     */
    default String getUsername() {
        return null;
    }

    /**
     * Get password.
     *
     * @return
     */
    default String getPassword() {
        return null;
    }

    /**
     * Get namespace.
     *
     * @return
     */
    default String getNamespace() {
        return null;
    }

    /**
     * Get item id.
     *
     * @return
     */
    default String getItemId() {
        return null;
    }

    /**
     * Get server addr.
     *
     * @return
     */
    default String getServerAddr() {
        return null;
    }

    /**
     * Get banner.
     *
     * @return
     */
    default Boolean getBanner() {
        return null;
    }

}
