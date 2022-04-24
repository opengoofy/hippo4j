package cn.hippo4j.common.design.builder;

import java.io.Serializable;

/**
 * Builder pattern interface definition.
 *
 * @author chen.ma
 * @date 2021/7/5 21:39
 */
public interface Builder<T> extends Serializable {

    /**
     * Build.
     *
     * @return
     */
    T build();

}
