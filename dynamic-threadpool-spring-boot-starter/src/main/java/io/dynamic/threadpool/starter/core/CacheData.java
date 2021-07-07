package io.dynamic.threadpool.starter.core;

import io.dynamic.threadpool.common.toolkit.ContentUtil;
import io.dynamic.threadpool.common.toolkit.Md5Util;
import io.dynamic.threadpool.common.constant.Constants;
import io.dynamic.threadpool.starter.listener.Listener;
import io.dynamic.threadpool.starter.wrap.ManagerListenerWrap;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Cache Data.
 *
 * @author chen.ma
 * @date 2021/6/22 20:46
 */
@Slf4j
public class CacheData {

    public volatile String md5;

    public volatile String content;

    public final String tenantId;

    public final String itemId;

    public final String tpId;

    private int taskId;

    private volatile long localConfigLastModified;

    private final CopyOnWriteArrayList<ManagerListenerWrap> listeners;

    public CacheData(String tenantId, String itemId, String tpId) {
        this.tenantId = tenantId;
        this.itemId = itemId;
        this.tpId = tpId;
        this.content = ContentUtil.getPoolContent(GlobalThreadPoolManage.getPoolParameter(tpId));
        this.md5 = getMd5String(content);
        this.listeners = new CopyOnWriteArrayList();

    }

    public void addListener(Listener listener) {
        if (null == listener) {
            throw new IllegalArgumentException("listener is null");
        }

        ManagerListenerWrap managerListenerWrap = new ManagerListenerWrap(md5, listener);

        if (listeners.addIfAbsent(managerListenerWrap)) {
            log.info("[add-listener] ok, tpId :: {}, cnt :: {}", tpId, listeners.size());
        }
    }

    public void checkListenerMd5() {
        for (ManagerListenerWrap wrap : listeners) {
            if (!md5.equals(wrap.getLastCallMd5())) {
                safeNotifyListener(content, md5, wrap);
            }
        }
    }

    private void safeNotifyListener(String content, String md5, ManagerListenerWrap wrap) {
        Listener listener = wrap.getListener();

        Runnable runnable = () -> {
            wrap.setLastCallMd5(md5);

            listener.receiveConfigInfo(content);
        };

        listener.getExecutor().execute(runnable);
    }

    public void setContent(String content) {
        this.content = content;
        this.md5 = getMd5String(this.content);
    }

    public static String getMd5String(String config) {
        return (null == config) ? Constants.NULL : Md5Util.md5Hex(config, Constants.ENCODE);
    }

    public String getMd5() {
        return this.md5;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }
}
