package cn.hippo4j.core.executor.web;

import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.toolkit.ByteConvertUtil;
import cn.hippo4j.core.executor.state.AbstractThreadPoolRuntime;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.RuntimeInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * Web thread pool run state handler.
 *
 * @author chen.ma
 * @date 2022/1/19 21:05
 */
@Slf4j
public class WebThreadPoolRunStateHandler extends AbstractThreadPoolRuntime {

    @Override
    protected PoolRunStateInfo supplement(PoolRunStateInfo poolRunStateInfo) {
        // 内存占比: 使用内存 / 最大内存
        RuntimeInfo runtimeInfo = new RuntimeInfo();
        String memoryProportion = StrUtil.builder(
                "已分配: ",
                ByteConvertUtil.getPrintSize(runtimeInfo.getTotalMemory()),
                " / 最大可用: ",
                ByteConvertUtil.getPrintSize(runtimeInfo.getMaxMemory())
        ).toString();

        poolRunStateInfo.setCurrentLoad(poolRunStateInfo.getCurrentLoad() + "%");
        poolRunStateInfo.setPeakLoad(poolRunStateInfo.getPeakLoad() + "%");

        poolRunStateInfo.setMemoryProportion(memoryProportion);
        poolRunStateInfo.setFreeMemory(ByteConvertUtil.getPrintSize(runtimeInfo.getFreeMemory()));

        return poolRunStateInfo;
    }

}
