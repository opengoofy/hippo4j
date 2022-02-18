package cn.hippo4j.starter.core;

import cn.hippo4j.starter.wrapper.ManagerListenerWrapper;
import cn.hippo4j.common.toolkit.ContentUtil;
import cn.hippo4j.common.toolkit.Md5Util;
import cn.hippo4j.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Cache data.
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

    private volatile boolean isInitializing = true;

    private volatile long localConfigLastModified;

    private final CopyOnWriteArrayList<ManagerListenerWrapper> listeners;

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
            throw new IllegalArgumentException("Listener is null.");
        }

        ManagerListenerWrapper managerListenerWrap = new ManagerListenerWrapper(md5, listener);
        if (listeners.addIfAbsent(managerListenerWrap)) {
            log.info("Add listener status :: ok, thread pool id :: {}, listeners count :: {}", tpId, listeners.size());
        }
    }

    public void checkListenerMd5() {
        for (ManagerListenerWrapper wrap : listeners) {
            if (!md5.equals(wrap.getLastCallMd5())) {
                safeNotifyListener(content, md5, wrap);
            }
        }
    }

    private void safeNotifyListener(String content, String md5, ManagerListenerWrapper wrap) {
        Listener listener = wrap.getListener();

        Runnable runnable = () -> {
            wrap.setLastCallMd5(md5);
            listener.receiveConfigInfo(content);
        };

        try {
            listener.getExecutor().execute(runnable);
        } catch (Exception ex) {
            log.error("Failed to execute listener. message :: {}", ex.getMessage());
        }
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

    public boolean isInitializing() {
        return isInitializing;
    }

    public void setInitializing(boolean isInitializing) {
        this.isInitializing = isInitializing;
    }

}
