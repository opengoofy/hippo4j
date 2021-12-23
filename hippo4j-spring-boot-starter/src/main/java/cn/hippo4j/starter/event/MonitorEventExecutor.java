package cn.hippo4j.starter.event;

import cn.hippo4j.common.function.NoArgsConsumer;
import cn.hippo4j.starter.toolkit.thread.QueueTypeEnum;
import cn.hippo4j.starter.toolkit.thread.ThreadPoolBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.common.constant.Constants.AVAILABLE_PROCESSORS;

/**
 * 动态线程池监控事件执行器.
 *
 * @author chen.ma
 * @date 2021/11/8 23:44
 */
@Slf4j
public class MonitorEventExecutor {

    private static final ExecutorService EVENT_EXECUTOR = ThreadPoolBuilder.builder()
            .threadFactory("client.monitor.event.executor")
            .corePoolSize(AVAILABLE_PROCESSORS)
            .maxPoolNum(AVAILABLE_PROCESSORS)
            .workQueue(QueueTypeEnum.LINKED_BLOCKING_QUEUE)
            .capacity(4096)
            .rejected(new ThreadPoolExecutor.AbortPolicy())
            .build();

    /**
     * 发布事件.
     *
     * @param consumer
     */
    public static void publishEvent(NoArgsConsumer consumer) {
        try {
            EVENT_EXECUTOR.execute(consumer::accept);
        } catch (RejectedExecutionException ex) {
            log.error("Monitoring thread pool run events exceeded load.");
        }
    }

}
