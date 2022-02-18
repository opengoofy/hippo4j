package cn.hippo4j.starter.toolkit.inet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Inet utils properties.
 *
 * @author chen.ma
 * @date 2021/11/12 21:34
 */
@ConfigurationProperties(InetUtilsProperties.PREFIX)
public class InetUtilsProperties {

    /**
     * Prefix for the Inet Utils properties.
     */
    public static final String PREFIX = "spring.cloud.inetutils";

    /**
     * The default hostname. Used in case of errors.
     */
    private String defaultHostname = "localhost";

    /**
     * The default IP address. Used in case of errors.
     */
    private String defaultIpAddress = "127.0.0.1";

    /**
     * Timeout, in seconds, for calculating hostname.
     */
    @Value("${spring.util.timeout.sec:${SPRING_UTIL_TIMEOUT_SEC:1}}")
    private int timeoutSeconds = 1;

    /**
     * List of Java regular expressions for network interfaces that will be ignored.
     */
    private List<String> ignoredInterfaces = new ArrayList<>();

    /**
     * Whether to use only interfaces with site local addresses. See
     * {@link InetAddress#isSiteLocalAddress()} for more details.
     */
    private boolean useOnlySiteLocalInterfaces = false;

    /**
     * List of Java regular expressions for network addresses that will be preferred.
     */
    private List<String> preferredNetworks = new ArrayList<>();

    public static String getPREFIX() {
        return PREFIX;
    }

    public String getDefaultHostname() {
        return this.defaultHostname;
    }

    public void setDefaultHostname(String defaultHostname) {
        this.defaultHostname = defaultHostname;
    }

    public String getDefaultIpAddress() {
        return this.defaultIpAddress;
    }

    public void setDefaultIpAddress(String defaultIpAddress) {
        this.defaultIpAddress = defaultIpAddress;
    }

    public int getTimeoutSeconds() {
        return this.timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public List<String> getIgnoredInterfaces() {
        return this.ignoredInterfaces;
    }

    public void setIgnoredInterfaces(List<String> ignoredInterfaces) {
        this.ignoredInterfaces = ignoredInterfaces;
    }

    public boolean isUseOnlySiteLocalInterfaces() {
        return this.useOnlySiteLocalInterfaces;
    }

    public void setUseOnlySiteLocalInterfaces(boolean useOnlySiteLocalInterfaces) {
        this.useOnlySiteLocalInterfaces = useOnlySiteLocalInterfaces;
    }

    public List<String> getPreferredNetworks() {
        return this.preferredNetworks;
    }

    public void setPreferredNetworks(List<String> preferredNetworks) {
        this.preferredNetworks = preferredNetworks;
    }

}
