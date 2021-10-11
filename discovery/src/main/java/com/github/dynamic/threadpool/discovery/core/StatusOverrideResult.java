package com.github.dynamic.threadpool.discovery.core;

import com.github.dynamic.threadpool.common.model.InstanceInfo.InstanceStatus;

/**
 * Status override result.
 *
 * @author chen.ma
 * @date 2021/8/8 23:11
 */
public class StatusOverrideResult {

    public static StatusOverrideResult NO_MATCH = new StatusOverrideResult(false, null);

    public static StatusOverrideResult matchingStatus(InstanceStatus status) {
        return new StatusOverrideResult(true, status);
    }

    private final boolean matches;

    private final InstanceStatus status;

    private StatusOverrideResult(boolean matches, InstanceStatus status) {
        this.matches = matches;
        this.status = status;
    }

    public boolean matches() {
        return matches;
    }

    public InstanceStatus status() {
        return status;
    }

}
