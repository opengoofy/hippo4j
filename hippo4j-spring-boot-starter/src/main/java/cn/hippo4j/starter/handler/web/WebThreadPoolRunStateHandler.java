package cn.hippo4j.starter.handler.web;

import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.starter.handler.AbstractThreadPoolRuntime;

/**
 * Web thread pool run state handler.
 *
 * @author chen.ma
 * @date 2022/1/19 21:05
 */
public class WebThreadPoolRunStateHandler extends AbstractThreadPoolRuntime {

    @Override
    protected PoolRunStateInfo supplement(PoolRunStateInfo basePoolRunStateInfo) {
        return basePoolRunStateInfo;
    }

}
