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

package cn.hippo4j.core.springboot.starter.refresher;

import cn.hippo4j.core.executor.manage.GlobalNotifyAlarmManage;
import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;
import com.google.common.base.Charsets;
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

import java.util.List;
import java.util.Map;

/**
 * @author Redick01
 * @date 2022/3/14 16:03
 */
@Slf4j
public class ZookeeperRefresherHandler extends AbstractCoreThreadPoolDynamicRefresh {

    private CuratorFramework curatorFramework;

    @Override
    public void afterPropertiesSet() {
        Map<String, String> zkConfigs = bootstrapCoreProperties.getZookeeper();
        curatorFramework = CuratorFrameworkFactory.newClient(zkConfigs.get("zk-connect-str"),
                new ExponentialBackoffRetry(1000, 3));
        String nodePath = ZKPaths.makePath(ZKPaths.makePath(zkConfigs.get("root-node"),
                zkConfigs.get("config-version")), zkConfigs.get("node"));
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
                    value = new String(data.watched().forPath(n), Charsets.UTF_8);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                content.append(nodeName).append("=").append(value).append("\n");
            });
            dynamicRefresh(content.toString());
            registerNotifyAlarmManage();
        } catch (Exception e) {
            log.error("load zk node error, nodePath is {}", nodePath, e);
        }
    }

    /**
     * Register notify alarm manage.
     */
    public void registerNotifyAlarmManage() {
        bootstrapCoreProperties.getExecutors().forEach(executorProperties -> {
            ThreadPoolNotifyAlarm threadPoolNotifyAlarm = new ThreadPoolNotifyAlarm(
                    executorProperties.getNotify().getIsAlarm(),
                    executorProperties.getNotify().getCapacityAlarm(),
                    executorProperties.getNotify().getActiveAlarm());
            threadPoolNotifyAlarm.setInterval(executorProperties.getNotify().getInterval());
            threadPoolNotifyAlarm.setReceives(executorProperties.receives());
            GlobalNotifyAlarmManage.put(executorProperties.getThreadPoolId(), threadPoolNotifyAlarm);
        });
    }
}
