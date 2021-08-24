package com.github.dynamic.threadpool.registry.core;

import com.github.dynamic.threadpool.common.model.InstanceInfo;

import java.util.List;

/**
 * Instance registry.
 *
 * @author chen.ma
 * @date 2021/8/8 22:31
 */
public interface InstanceRegistry<T> {

    /**
     * List instance.
     *
     * @param appName
     * @return
     */
    List<Lease<T>> listInstance(String appName);

    /**
     * Register.
     *
     * @param info
     */
    void register(T info);

    /**
     * Renew.
     *
     * @param instanceRenew
     * @return
     */
    boolean renew(InstanceInfo.InstanceRenew instanceRenew);

}
