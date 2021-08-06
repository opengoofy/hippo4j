package com.github.dynamic.threadpool.starter.core;

import lombok.Getter;
import lombok.Setter;

/**
 * Instance Info.
 *
 * @author chen.ma
 * @date 2021/7/13 22:10
 */
@Getter
@Setter
public class InstanceInfo implements InstanceConfig {

    private static final String UNKNOWN = "unknown";

    private String appName = UNKNOWN;

    private String hostName;

    private String instanceId;

}

