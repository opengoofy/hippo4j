package cn.hippo4j.starter.test;

import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.monitor.collect.RunTimeInfoCollector;
import cn.hippo4j.starter.toolkit.thread.ResizableCapacityLinkedBlockIngQueue;
import cn.hippo4j.starter.toolkit.thread.ThreadFactoryBuilder;
import cn.hippo4j.starter.toolkit.thread.ThreadPoolBuilder;
import cn.hippo4j.starter.toolkit.thread.ThreadUtil;
import cn.hippo4j.starter.wrapper.DynamicThreadPoolWrapper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 监控性能测试.
 *
 * @author chen.ma
 * @date 2022/1/12 08:17
 */
@Slf4j
public class MonitorPerformanceTest {

    private static final int MAX_EXECUTE = 20000;

    private static final String TEST_FLAG = "test.monitor.performance";

    private static final RunTimeInfoCollector COLLECTOR = new RunTimeInfoCollector(null);

    private static final ThreadPoolExecutor DYNAMIC_EXECUTOR = ThreadPoolBuilder.builder()
            .dynamicPool()
            .threadFactory(TEST_FLAG)
            .poolThreadSize(5, 10)
            .workQueue(new ResizableCapacityLinkedBlockIngQueue(1024))
            .rejected(new ThreadPoolExecutor.AbortPolicy())
            .build();

    private static final ThreadPoolExecutor MONITOR_DYNAMIC_EXECUTOR = ThreadPoolBuilder.builder()
            .dynamicPool()
            .threadFactory("dynamic.thread.pool")
            .poolThreadSize(5, 10)
            .workQueue(new ResizableCapacityLinkedBlockIngQueue(1024))
            .rejected(new ThreadPoolExecutor.AbortPolicy())
            .build();

    private static final ScheduledThreadPoolExecutor COLLECT_VESSEL_EXECUTOR = new ScheduledThreadPoolExecutor(
            new Integer(1),
            ThreadFactoryBuilder.builder().daemon(true).prefix("client.scheduled.collect.dat").build()
    );

    static {
        DynamicThreadPoolWrapper wrapper = new DynamicThreadPoolWrapper(TEST_FLAG, DYNAMIC_EXECUTOR);
        GlobalThreadPoolManage.registerPool(TEST_FLAG, wrapper);
    }

    public static void main(String[] args) {
        log.info("测试线程池, 执行时长 :: {}", testExecutorTime(DYNAMIC_EXECUTOR));

        log.info("开始执行 DYNAMIC_EXECUTOR 线程池销毁...");
        while (!DYNAMIC_EXECUTOR.isTerminated()) {
            ThreadUtil.sleep(500);
        }

        COLLECT_VESSEL_EXECUTOR.scheduleWithFixedDelay(
                () -> {
                    PoolRunStateInfo poolRunState = COLLECTOR.getPoolRunState(TEST_FLAG);
                    log.info("采集数据 :: {}", JSONUtil.toJSONString(poolRunState));
                },
                1000,
                1000,
                TimeUnit.MILLISECONDS
        );

        log.info("测试带有监控线程池, 执行时长 :: {}", testExecutorTime(MONITOR_DYNAMIC_EXECUTOR));

        log.info("开始执行 MONITOR_DYNAMIC_EXECUTOR 线程池销毁...");
        while (!MONITOR_DYNAMIC_EXECUTOR.isTerminated()) {
            ThreadUtil.sleep(500);
        }

        COLLECT_VESSEL_EXECUTOR.shutdownNow();
    }

    private static Long testExecutorTime(ThreadPoolExecutor executor) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < MAX_EXECUTE; i++) {
            try {
                ThreadUtil.sleep(1);
                executor.execute(() -> {
                    ThreadUtil.sleep(5);
                });
            } catch (Exception ex) {
                // ignore
                log.error("拒绝策略执行.");
            }
        }

        long endTime = System.currentTimeMillis();

        executor.shutdown();
        return endTime - startTime;
    }

}
