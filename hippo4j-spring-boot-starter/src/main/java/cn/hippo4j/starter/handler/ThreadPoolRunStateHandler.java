package cn.hippo4j.starter.handler;

import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.toolkit.ByteConvertUtil;
import cn.hippo4j.starter.wrapper.DynamicThreadPoolWrapper;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.RuntimeInfo;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread pool run state service.
 *
 * @author chen.ma
 * @date 2021/7/12 21:25
 */
@Slf4j
public class ThreadPoolRunStateHandler extends AbstractThreadPoolRuntime {

    private static InetAddress INET_ADDRESS;

    static {
        try {
            INET_ADDRESS = InetAddress.getLocalHost();
        } catch (UnknownHostException ex) {
            log.error("Local IP acquisition failed.", ex);
        }
    }

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
        poolRunStateInfo.setHost(INET_ADDRESS.getHostAddress());
        poolRunStateInfo.setMemoryProportion(memoryProportion);
        poolRunStateInfo.setFreeMemory(ByteConvertUtil.getPrintSize(runtimeInfo.getFreeMemory()));

        String threadPoolId = poolRunStateInfo.getTpId();
        DynamicThreadPoolWrapper executorService = GlobalThreadPoolManage.getExecutorService(threadPoolId);
        ThreadPoolExecutor pool = executorService.getExecutor();
        String rejectedName = pool.getRejectedExecutionHandler().getClass().getSimpleName();
        poolRunStateInfo.setRejectedName(rejectedName);

        return poolRunStateInfo;
    }

}
