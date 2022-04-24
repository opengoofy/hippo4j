package cn.hippo4j.starter.wrapper;

import cn.hippo4j.starter.core.Listener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Manager listener wrapper.
 *
 * @author chen.ma
 * @date 2021/6/22 17:47
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerListenerWrapper {

    /**
     * Last call md5
     */
    private String lastCallMd5;

    /**
     * Listener
     */
    private Listener listener;

}
