package cn.hippo4j.core.executor.support;

import cn.hippo4j.core.spi.CustomRejectedExecutionHandler;
import cn.hippo4j.core.spi.DynamicThreadPoolServiceLoader;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

/**
 * Reject policy type Enum.
 *
 * @author chen.ma
 * @date 2021/7/10 23:16
 */
public enum RejectedTypeEnum {

    /**
     * 被拒绝任务的程序由主线程执行
     */
    CALLER_RUNS_POLICY(1, "CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy()),

    /**
     * 被拒绝任务的处理程序, 抛出异常
     */
    ABORT_POLICY(2, "AbortPolicy", new ThreadPoolExecutor.AbortPolicy()),

    /**
     * 被拒绝任务的处理程序, 默默地丢弃被拒绝的任务。
     */
    DISCARD_POLICY(3, "DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy()),

    /**
     * 被拒绝任务的处理程序, 它丢弃最早的未处理请求, 然后重试
     */
    DISCARD_OLDEST_POLICY(4, "DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy()),

    /**
     * 发生拒绝事件时, 添加新任务并运行最早的任务
     */
    RUNS_OLDEST_TASK_POLICY(5, "RunsOldestTaskPolicy", new RejectedPolicies.RunsOldestTaskPolicy()),

    /**
     * 使用阻塞方法将拒绝任务添加队列, 可保证任务不丢失
     */
    SYNC_PUT_QUEUE_POLICY(6, "SyncPutQueuePolicy", new RejectedPolicies.SyncPutQueuePolicy());

    /**
     * 类型
     */
    public Integer type;

    /**
     * 名称
     */
    public String name;

    /**
     * 线程池拒绝策略
     */
    public RejectedExecutionHandler rejectedHandler;

    RejectedTypeEnum(Integer type, String name, RejectedExecutionHandler rejectedHandler) {
        this.type = type;
        this.name = name;
        this.rejectedHandler = rejectedHandler;
    }

    static {
        DynamicThreadPoolServiceLoader.register(CustomRejectedExecutionHandler.class);
    }

    /**
     * Create policy.
     *
     * @param name
     * @return
     */
    public static RejectedExecutionHandler createPolicy(String name) {
        RejectedTypeEnum rejectedTypeEnum = Stream.of(RejectedTypeEnum.values())
                .filter(each -> Objects.equals(each.name, name))
                .findFirst()
                .orElse(null);

        if (rejectedTypeEnum != null) {
            return rejectedTypeEnum.rejectedHandler;
        }

        Collection<CustomRejectedExecutionHandler> customRejectedExecutionHandlers = DynamicThreadPoolServiceLoader
                .getSingletonServiceInstances(CustomRejectedExecutionHandler.class);
        Optional<RejectedExecutionHandler> customRejected = customRejectedExecutionHandlers.stream()
                .filter(each -> Objects.equals(name, each.getName()))
                .map(each -> each.generateRejected())
                .findFirst();

        return customRejected.orElse(ABORT_POLICY.rejectedHandler);
    }

    /**
     * Create policy.
     *
     * @param type
     * @return
     */
    public static RejectedExecutionHandler createPolicy(int type) {
        Optional<RejectedExecutionHandler> rejectedTypeEnum = Stream.of(RejectedTypeEnum.values())
                .filter(each -> Objects.equals(type, each.type))
                .map(each -> each.rejectedHandler)
                .findFirst();

        // 使用 SPI 匹配拒绝策略
        RejectedExecutionHandler resultRejected = rejectedTypeEnum.orElseGet(() -> {
            Collection<CustomRejectedExecutionHandler> customRejectedExecutionHandlers = DynamicThreadPoolServiceLoader
                    .getSingletonServiceInstances(CustomRejectedExecutionHandler.class);
            Optional<RejectedExecutionHandler> customRejected = customRejectedExecutionHandlers.stream()
                    .filter(each -> Objects.equals(type, each.getType()))
                    .map(each -> each.generateRejected())
                    .findFirst();

            return customRejected.orElse(ABORT_POLICY.rejectedHandler);
        });

        return resultRejected;
    }

    /**
     * Get rejected name by type.
     *
     * @param type
     * @return
     */
    public static String getRejectedNameByType(int type) {
        return createPolicy(type).getClass().getSimpleName();
    }

}
