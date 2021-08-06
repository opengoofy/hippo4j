package com.github.dynamic.threadpool.starter.toolkit.thread;

import java.io.Serializable;

/**
 * Builder pattern interface definition.
 *
 * @author chen.ma
 * @date 2021/7/5 21:39
 */
public interface Builder<T> extends Serializable {

    /**
     * 构建
     *
     * @return 被构建的对象
     */
    T build();

}
