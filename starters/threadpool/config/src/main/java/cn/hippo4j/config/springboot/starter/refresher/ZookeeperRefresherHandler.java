/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.config.springboot.starter.refresher;

import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.message.core.service.GlobalNotifyAlarmManage;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolNotifyAlarm;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.api.GetDataBuilder;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.WatchedEvent;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Zookeeper refresher handler.
 */
@Slf4j
public class ZookeeperRefresherHandler extends AbstractConfigThreadPoolDynamicRefresh {

    private static final String ZK_CONNECT_STR = "zk-connect-str";

    private static final String ROOT_NODE = "root-node";

    private static final String CONFIG_VERSION = "config-version";

    private static final String NODE = "node";

    private CuratorFramework curatorFramework;

    private static final int BASE_SLEEP_TIME_MS = 1000;

    private static final int MAX_RETRIES = 3;

    @Override
    public void registerListener() {
        BootstrapConfigProperties actualBootstrapConfigProperties = (BootstrapConfigProperties) bootstrapConfigProperties;
        Map<String, String> zkConfigs = actualBootstrapConfigProperties.getZookeeper();
        curatorFramework = CuratorFrameworkFactory.newClient(zkConfigs.get(ZK_CONNECT_STR),
                new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        String nodePath = ZKPaths.makePath(ZKPaths.makePath(zkConfigs.get(ROOT_NODE),
                zkConfigs.get(CONFIG_VERSION)), zkConfigs.get(NODE));
        final ConnectionStateListener connectionStateListener = (client, newState) -> {
            if (newState == ConnectionState.CONNECTED) {
                loadNode(nodePath);
            } else if (newState == ConnectionState.RECONNECTED) {
                loadNode(nodePath);
            }
        };
        final CuratorListener curatorListener = (client, curatorEvent) -> {
            final WatchedEvent watchedEvent = curatorEvent.getWatchedEvent();
            if (null != watchedEvent) {
                switch (watchedEvent.getType()) {
                    case NodeChildrenChanged:
                    case NodeDataChanged:
                        loadNode(nodePath);
                        break;
                    default:
                        break;
                }
            }
        };
        curatorFramework.getConnectionStateListenable().addListener(connectionStateListener);
        curatorFramework.getCuratorListenable().addListener(curatorListener);
        curatorFramework.start();
    }

    /**
     * Load config info and refresh.
     *
     * @param nodePath zk config node path.
     */
    public void loadNode(String nodePath) {
        String content = nodePathResolver(nodePath);
        if (content != null) {
            dynamicRefresh(content);
            registerNotifyAlarmManage();
        }
    }

    /**
     * resolver for zk config
     *
     * @param nodePath zk config node path
     * @return resolver result
     */
    private String nodePathResolver(String nodePath) {
        try {
            final GetChildrenBuilder childrenBuilder = curatorFramework.getChildren();
            final List<String> children = childrenBuilder.watched().forPath(nodePath);
            StringBuilder content = new StringBuilder();
            children.forEach(c -> {
                String n = ZKPaths.makePath(nodePath, c);
                final String nodeName = ZKPaths.getNodeFromPath(n);
                final GetDataBuilder data = curatorFramework.getData();
                String value = "";
                try {
                    value = new String(data.watched().forPath(n), StandardCharsets.UTF_8);
                } catch (Exception ex) {
                    log.error("Load zookeeper node error", ex);
                }
                content.append(nodeName).append("=").append(value).append("\n");
            });
            return content.toString();
        } catch (Exception ex) {
            log.error("Load zookeeper node error, nodePath is: {}", nodePath, ex);
            return null;
        }
    }

    /**
     * Register notify alarm manage.
     */
    public void registerNotifyAlarmManage() {
        BootstrapConfigProperties actualBootstrapConfigProperties = (BootstrapConfigProperties) bootstrapConfigProperties;
        actualBootstrapConfigProperties.getExecutors().forEach(executorProperties -> {
            ThreadPoolNotifyAlarm threadPoolNotifyAlarm = new ThreadPoolNotifyAlarm(
                    executorProperties.getAlarm(),
                    executorProperties.getCapacityAlarm(),
                    executorProperties.getActiveAlarm());
            threadPoolNotifyAlarm.setInterval(executorProperties.getNotify().getInterval());
            threadPoolNotifyAlarm.setReceives(executorProperties.getNotify().getReceives());
            GlobalNotifyAlarmManage.put(executorProperties.getThreadPoolId(), threadPoolNotifyAlarm);
        });
    }
}
