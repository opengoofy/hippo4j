package com.github.dynamic.threadpool.starter.wrap;

import com.github.dynamic.threadpool.starter.core.Listener;
import lombok.Getter;
import lombok.Setter;

/**
 * Manager listener wrap.
 *
 * @author chen.ma
 * @date 2021/6/22 17:47
 */
@Getter
@Setter
public class ManagerListenerWrap {

    private String lastCallMd5;

    final Listener listener;

    public ManagerListenerWrap(String md5, Listener listener) {
        this.lastCallMd5 = md5;
        this.listener = listener;
    }

}
