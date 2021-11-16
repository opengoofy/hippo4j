package cn.hippo4j.starter.event;

import cn.hippo4j.common.function.NoArgsConsumer;
import cn.hippo4j.starter.toolkit.thread.QueueTypeEnum;
import cn.hippo4j.starter.toolkit.thread.ThreadPoolBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 事件执行器.
 *
 * @author chen.ma
 * @date 2021/11/8 23:44
 */
public class EventExecutor {

    private static final ExecutorService EVENT_EXECUTOR = ThreadPoolBuilder.builder()
            .threadFactory("event-executor")
            .corePoolSize(Runtime.getRuntime().availableProcessors())
            .maxPoolNum(Runtime.getRuntime().availableProcessors())
            .workQueue(QueueTypeEnum.ARRAY_BLOCKING_QUEUE, 2048)
            .rejected(new ThreadPoolExecutor.DiscardPolicy())
            .build();

    /**
     * 发布事件.
     *
     * @param consumer
     */
    public static void publishEvent(NoArgsConsumer consumer) {
        EVENT_EXECUTOR.execute(consumer::accept);
    }

}
