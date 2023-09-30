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

package cn.hippo4j.springboot.starter.core;

import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.springboot.starter.wrapper.ManagerListenerWrapper;
import cn.hippo4j.common.toolkit.ContentUtil;
import cn.hippo4j.common.toolkit.Md5Util;
import cn.hippo4j.common.constant.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Cache data.
 */
@Slf4j
public class CacheData {

    @Getter
    private volatile String md5;

    private volatile String content;

    @Getter
    private final String tenantId;

    @Getter
    private final String itemId;

    @Getter
    private final String threadPoolId;

    @Setter
    private volatile boolean isInitializing = true;

    private final CopyOnWriteArrayList<ManagerListenerWrapper> listeners;

    public CacheData(String tenantId, String itemId, String threadPoolId) {
        this.tenantId = tenantId;
        this.itemId = itemId;
        this.threadPoolId = threadPoolId;
        this.content = ContentUtil.getPoolContent(ThreadPoolExecutorRegistry.getHolder(threadPoolId).getParameterInfo());
        this.md5 = getMd5String(content);
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public void addListener(Listener listener) {
        if (null == listener) {
            throw new IllegalArgumentException("Listener is null.");
        }
        ManagerListenerWrapper managerListenerWrap = new ManagerListenerWrapper(md5, listener);
        if (listeners.addIfAbsent(managerListenerWrap)) {
            log.info("Add listener status: ok, thread pool id: {}, listeners count: {}", threadPoolId, listeners.size());
        }
    }

    public void checkListenerMd5() {
        for (ManagerListenerWrapper managerListenerWrapper : listeners) {
            if (!md5.equals(managerListenerWrapper.getLastCallMd5())) {
                safeNotifyListener(content, md5, managerListenerWrapper);
            }
        }
    }

    private void safeNotifyListener(String content, String md5, ManagerListenerWrapper managerListenerWrapper) {
        Listener listener = managerListenerWrapper.getListener();
        Runnable runnable = () -> {
            managerListenerWrapper.setLastCallMd5(md5);
            listener.receiveConfigInfo(content);
        };
        try {
            listener.getExecutor().execute(runnable);
        } catch (Exception ex) {
            log.error("Failed to execute listener. message: {}", ex.getMessage());
        }
    }

    public void setContent(String content) {
        this.content = content;
        this.md5 = getMd5String(this.content);
    }

    public static String getMd5String(String config) {
        return (null == config) ? Constants.NULL : Md5Util.md5Hex(config, Constants.ENCODE);
    }

    public boolean isInitializing() {
        return isInitializing;
    }
}
