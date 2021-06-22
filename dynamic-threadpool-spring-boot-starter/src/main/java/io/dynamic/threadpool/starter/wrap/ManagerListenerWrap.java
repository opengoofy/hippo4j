package io.dynamic.threadpool.starter.wrap;

import io.dynamic.threadpool.starter.listener.Listener;
import lombok.Getter;
import lombok.Setter;

/**
 * 监听包装
 *
 * @author chen.ma
 * @date 2021/6/22 17:47
 */
@Getter
@Setter
public class ManagerListenerWrap {

    final Listener listener;

    String lastCallMd5;

    public ManagerListenerWrap(String md5, Listener listener) {
        this.lastCallMd5 = md5;
        this.listener = listener;
    }
}
