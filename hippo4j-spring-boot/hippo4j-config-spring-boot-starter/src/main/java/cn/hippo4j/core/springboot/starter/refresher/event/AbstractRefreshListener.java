package cn.hippo4j.core.springboot.starter.refresher.event;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Objects;

/**
 * Refresh listener abstract base class.
 */
@Log4j2
public abstract class AbstractRefreshListener<M> implements RefreshListener<Hippo4jConfigDynamicRefreshEvent, M> {

    protected static final String PORT_PROPERTY = "local.server.port";

    protected static final String ALL = "*";

    /**
     * separator
     */
    protected static final String SEPARATOR = ",";

    /**
     * application ip
     */
    protected final String ipAddress;

    /**
     * application post
     */
    protected final String port;

    AbstractRefreshListener() {
        InetUtils inetUtils = ApplicationContextHolder.getBean(InetUtils.class);
        InetUtils.HostInfo loopbackHostInfo = inetUtils.findFirstNonLoopbackHostInfo();
        Assert.isNull(loopbackHostInfo, "Unable to get the application IP address");
        ipAddress = loopbackHostInfo.getIpAddress();
        Environment environment = ApplicationContextHolder.getInstance().getEnvironment();
        Assert.isTrue(environment.containsProperty(PORT_PROPERTY), "Unable to get the application port");
        port = environment.getProperty(PORT_PROPERTY);
    }

    /**
     * Matching nodes<br>
     * nodes is ip + port.Get 'nodes' in the new Properties,Compare this with the ip + port of Application.<br>
     * Support prefix pattern matching.e.g: <br>
     * <ul>
     *     <li>192.168.1.5:* -- Matches all ports of 192.168.1.5</li>
     *     <li>192.168.1.*:2009 -- Matches 2009 port of 192.168.1.*</li>
     *     <li>* -- all</li>
     *     <li>empty -- all</li>
     * </ul>
     * The format of ip + port is ip : port.
     *
     * @param properties new Properties
     */
    @Override
    public boolean match(M properties) {
        return false;
    }

    /**
     * check all
     *
     * @param nodes nodes
     */
    protected boolean checkArray(String nodes) {
        if (StringUtil.isEmpty(nodes) || ALL.equals(nodes)) {
            return true;
        }
        String[] splitNodes = nodes.split(SEPARATOR);
        return Arrays.stream(splitNodes)
            .distinct()
            .map(IpAndPort::new)
            .map(i -> i.check(ipAddress, port))
            .anyMatch(Boolean.FALSE::equals);
    }

    /**
     * ip + port
     */
    @Data
    protected static class IpAndPort {

        protected static final String COLON = ";";
        private String ip;
        private String port;

        public IpAndPort(String node) {
            String[] ipPort = node.split(COLON);
            Assert.isTrue(ipPort.length != 2, "The IP address format is error:" + node);
            ip = ipPort[0];
            port = ipPort[1];
        }

        /**
         * check
         *
         * @param ip   application ip
         * @param port application port
         */
        public boolean check(String ip, String port) {
            return Objects.equals(ip, this.ip) && Objects.equals(port, this.port);
        }

    }

}