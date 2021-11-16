package cn.hippo4j.starter.wrapper;

import cn.hippo4j.starter.core.Listener;
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
