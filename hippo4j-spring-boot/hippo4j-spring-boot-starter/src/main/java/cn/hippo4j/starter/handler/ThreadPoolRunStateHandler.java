package cn.hippo4j.starter.handler;

import cn.hippo4j.common.model.ManyPoolRunStateInfo;
import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.toolkit.ByteConvertUtil;
import cn.hippo4j.starter.toolkit.inet.InetUtils;
import cn.hippo4j.starter.wrapper.DynamicThreadPoolWrapper;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.RuntimeInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.starter.config.DynamicThreadPoolAutoConfiguration.CLIENT_IDENTIFICATION_VALUE;

/**
 * Thread pool run state service.
 *
 * @author chen.ma
 * @date 2021/7/12 21:25
 */
@Slf4j
@AllArgsConstructor
public class ThreadPoolRunStateHandler extends AbstractThreadPoolRuntime {

    private final InetUtils hippo4JInetUtils;

    private final ConfigurableEnvironment environment;

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

        String ipAddress = hippo4JInetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        poolRunStateInfo.setHost(ipAddress);
        poolRunStateInfo.setMemoryProportion(memoryProportion);
        poolRunStateInfo.setFreeMemory(ByteConvertUtil.getPrintSize(runtimeInfo.getFreeMemory()));

        String threadPoolId = poolRunStateInfo.getTpId();
        DynamicThreadPoolWrapper executorService = GlobalThreadPoolManage.getExecutorService(threadPoolId);
        ThreadPoolExecutor pool = executorService.getExecutor();
        String rejectedName = pool.getRejectedExecutionHandler().getClass().getSimpleName();
        poolRunStateInfo.setRejectedName(rejectedName);

        ManyPoolRunStateInfo manyPoolRunStateInfo = BeanUtil.toBean(poolRunStateInfo, ManyPoolRunStateInfo.class);
        manyPoolRunStateInfo.setIdentify(CLIENT_IDENTIFICATION_VALUE);

        String active = environment.getProperty("spring.profiles.active", "UNKNOWN");
        manyPoolRunStateInfo.setActive(active.toUpperCase());

        String threadPoolState = ThreadPoolStatusHandler.getThreadPoolState(pool);
        manyPoolRunStateInfo.setState(threadPoolState);

        return manyPoolRunStateInfo;
    }

}
