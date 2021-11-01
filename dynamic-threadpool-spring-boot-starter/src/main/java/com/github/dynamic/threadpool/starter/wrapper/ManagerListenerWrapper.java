package com.github.dynamic.threadpool.starter.wrapper;

import com.github.dynamic.threadpool.starter.core.Listener;
import lombok.Getter;
import lombok.Setter;

/**
 * Manager listener wrapper.
 *
 * @author chen.ma
 * @date 2021/6/22 17:47
 */
@Getter
@Setter
public class ManagerListenerWrapper {

    private String lastCallMd5;

    final Listener listener;

    public ManagerListenerWrapper(String md5, Listener listener) {
        this.lastCallMd5 = md5;
        this.listener = listener;
    }

}
