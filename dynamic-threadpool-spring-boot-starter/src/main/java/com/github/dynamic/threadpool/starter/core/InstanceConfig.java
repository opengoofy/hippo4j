package com.github.dynamic.threadpool.starter.core;

/**
 * Dynamic thread pool instance configuration.
 *
 * @author chen.ma
 * @date 2021/8/6 21:31
 */
public interface InstanceConfig {

    /**
     * get Host Name.
     *
     * @return
     */
    String getHostName();

    /**
     * get Instance Id.
     *
     * @return
     */
    String getInstanceId();

}
