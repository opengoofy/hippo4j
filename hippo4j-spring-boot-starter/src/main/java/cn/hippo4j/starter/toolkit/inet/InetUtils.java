package cn.hippo4j.starter.toolkit.inet;

import cn.hippo4j.starter.toolkit.thread.ThreadPoolBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Inet utils.
 *
 * @author Spencer Gibb
 * @date 2021/11/12 21:33
 */
public class InetUtils implements Closeable {

    private final ExecutorService executorService;

    private final InetUtilsProperties properties;

    private final Log log = LogFactory.getLog(InetUtils.class);

    public InetUtils(InetUtilsProperties properties) {
        this.properties = properties;
        this.executorService = ThreadPoolBuilder.builder()
                .threadFactory(InetUtilsProperties.PREFIX, Boolean.TRUE)
                .corePoolSize(1)
                .build();
    }

    @Override
    public void close() {
        this.executorService.shutdown();
    }

    public HostInfo findFirstNonLoopbackHostInfo() {
        InetAddress address = findFirstNonLoopbackAddress();
        if (address != null) {
            return convertAddress(address);
        }
        HostInfo hostInfo = new HostInfo();
        hostInfo.setHostname(this.properties.getDefaultHostname());
        hostInfo.setIpAddress(this.properties.getDefaultIpAddress());
        return hostInfo;
    }

    public InetAddress findFirstNonLoopbackAddress() {
        InetAddress result = null;
        try {
            int lowest = Integer.MAX_VALUE;
            for (Enumeration<NetworkInterface> nics = NetworkInterface
                    .getNetworkInterfaces(); nics.hasMoreElements(); ) {
                NetworkInterface ifc = nics.nextElement();
                if (ifc.isUp()) {
                    this.log.trace("Testing interface: " + ifc.getDisplayName());
                    if (ifc.getIndex() < lowest || result == null) {
                        lowest = ifc.getIndex();
                    } else if (result != null) {
                        continue;
                    }

                    // @formatter:off
                    if (!ignoreInterface(ifc.getDisplayName())) {
                        for (Enumeration<InetAddress> addrs = ifc
                                .getInetAddresses(); addrs.hasMoreElements(); ) {
                            InetAddress address = addrs.nextElement();
                            if (address instanceof Inet4Address
                                    && !address.isLoopbackAddress()
                                    && isPreferredAddress(address)) {
                                this.log.trace("Found non-loopback interface: "
                                        + ifc.getDisplayName());
                                result = address;
                            }
                        }
                    }
                    // @formatter:on
                }
            }
        } catch (IOException ex) {
            this.log.error("Cannot get first non-loopback address", ex);
        }

        if (result != null) {
            return result;
        }

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            this.log.warn("Unable to retrieve localhost");
        }

        return null;
    }

    boolean isPreferredAddress(InetAddress address) {

        if (this.properties.isUseOnlySiteLocalInterfaces()) {
            final boolean siteLocalAddress = address.isSiteLocalAddress();
            if (!siteLocalAddress) {
                this.log.trace("Ignoring address: " + address.getHostAddress());
            }
            return siteLocalAddress;
        }
        final List<String> preferredNetworks = this.properties.getPreferredNetworks();
        if (preferredNetworks.isEmpty()) {
            return true;
        }
        for (String regex : preferredNetworks) {
            final String hostAddress = address.getHostAddress();
            if (hostAddress.matches(regex) || hostAddress.startsWith(regex)) {
                return true;
            }
        }
        this.log.trace("Ignoring address: " + address.getHostAddress());
        return false;
    }

    boolean ignoreInterface(String interfaceName) {
        for (String regex : this.properties.getIgnoredInterfaces()) {
            if (interfaceName.matches(regex)) {
                this.log.trace("Ignoring interface: " + interfaceName);
                return true;
            }
        }
        return false;
    }

    public HostInfo convertAddress(final InetAddress address) {
        HostInfo hostInfo = new HostInfo();
        Future<String> result = this.executorService.submit(address::getHostName);

        String hostname;
        try {
            hostname = result.get(this.properties.getTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (Exception e) {
            this.log.info("Cannot determine local hostname");
            hostname = "localhost";
        }
        hostInfo.setHostname(hostname);
        hostInfo.setIpAddress(address.getHostAddress());
        return hostInfo;
    }

    /**
     * Host information pojo.
     */
    public static class HostInfo {

        /**
         * Should override the host info.
         */
        public boolean override;

        private String ipAddress;

        private String hostname;

        public HostInfo(String hostname) {
            this.hostname = hostname;
        }

        public HostInfo() {
        }

        public int getIpAddressAsInt() {
            InetAddress inetAddress = null;
            String host = this.ipAddress;
            if (host == null) {
                host = this.hostname;
            }
            try {
                inetAddress = InetAddress.getByName(host);
            } catch (final UnknownHostException e) {
                throw new IllegalArgumentException(e);
            }
            return ByteBuffer.wrap(inetAddress.getAddress()).getInt();
        }

        public boolean isOverride() {
            return this.override;
        }

        public void setOverride(boolean override) {
            this.override = override;
        }

        public String getIpAddress() {
            return this.ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getHostname() {
            return this.hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

    }

}
