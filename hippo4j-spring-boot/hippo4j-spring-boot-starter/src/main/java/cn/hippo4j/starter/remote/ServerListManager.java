package cn.hippo4j.starter.remote;

import cn.hippo4j.starter.config.BootstrapProperties;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Server list manager.
 *
 * @author chen.ma
 * @date 2021/6/23 20:42
 */
@Slf4j
public class ServerListManager {

    private static final String HTTPS = "https://";

    private static final String HTTP = "http://";

    private String serverAddrsStr;

    @Getter
    volatile List<String> serverUrls = new ArrayList();

    private volatile String currentServerAddr;

    private Iterator<String> iterator;

    private final BootstrapProperties properties;

    public ServerListManager(BootstrapProperties dynamicThreadPoolProperties) {
        this.properties = dynamicThreadPoolProperties;
        serverAddrsStr = properties.getServerAddr();

        if (!StringUtils.isEmpty(serverAddrsStr)) {
            List<String> serverAddrList = new ArrayList();
            String[] serverAddrListArr = this.serverAddrsStr.split(",");

            for (String serverAddr : serverAddrListArr) {
                boolean whetherJoint = StrUtil.isNotBlank(serverAddr)
                        && !serverAddr.startsWith(HTTPS) && !serverAddr.startsWith(HTTP);
                if (whetherJoint) {
                    serverAddr = HTTP + serverAddr;
                }

                currentServerAddr = serverAddr;
                serverAddrList.add(serverAddr);
            }

            this.serverUrls = serverAddrList;
        }
    }

    public String getCurrentServerAddr() {
        if (StringUtils.isEmpty(currentServerAddr)) {
            iterator = iterator();
            currentServerAddr = iterator.next();
        }
        return currentServerAddr;
    }

    Iterator<String> iterator() {
        return new ServerAddressIterator(serverUrls);
    }

    private static class ServerAddressIterator implements Iterator<String> {

        final List<RandomizedServerAddress> sorted;

        final Iterator<RandomizedServerAddress> iter;

        public ServerAddressIterator(List<String> source) {
            sorted = new ArrayList();
            for (String address : source) {
                sorted.add(new RandomizedServerAddress(address));
            }
            Collections.sort(sorted);
            iter = sorted.iterator();
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public String next() {
            return null;
        }

        static class RandomizedServerAddress implements Comparable<RandomizedServerAddress> {

            static Random random = new Random();

            String serverIp;

            int priority = 0;

            int seed;

            public RandomizedServerAddress(String ip) {
                try {
                    this.serverIp = ip;
                    /*
                     change random scope from 32 to Integer.MAX_VALUE to fix load balance issue
                     */
                    this.seed = random.nextInt(Integer.MAX_VALUE);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public int compareTo(RandomizedServerAddress other) {
                if (this.priority != other.priority) {
                    return other.priority - this.priority;
                } else {
                    return other.seed - this.seed;
                }
            }
        }
    }

}
