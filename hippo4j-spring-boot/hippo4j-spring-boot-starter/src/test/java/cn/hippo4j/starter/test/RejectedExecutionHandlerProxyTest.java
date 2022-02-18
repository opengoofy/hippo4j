package cn.hippo4j.starter.test;

import cn.hippo4j.starter.core.DynamicThreadPoolExecutor;
import cn.hippo4j.starter.toolkit.thread.ThreadPoolBuilder;
import cn.hippo4j.starter.toolkit.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Rejected execution handler proxy test.
 *
 * @author chen.ma
 * @date 2022/2/17 19:52
 */
@Slf4j
public class RejectedExecutionHandlerProxyTest {

    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            test(i + "");
        }
    }

    private static void test(String threadPoolId) {
        ThreadPoolExecutor executor = ThreadPoolBuilder.builder()
                .threadPoolId(threadPoolId)
                .threadFactory(threadPoolId)
                .poolThreadSize(1, 1)
                .workQueue(new LinkedBlockingQueue(1))
                .dynamicPool()
                .build();


        for (int i = 0; i < 300; i++) {
            try {
                executor.execute(() -> ThreadUtil.sleep(Integer.MAX_VALUE));
            } catch (Exception ex) {
                log.error("ThreadPool name :: {}, Exception :: ", Thread.currentThread().getName(), ex);
            }
        }

        ThreadUtil.sleep(1000);

        DynamicThreadPoolExecutor dynamicThreadPoolExecutor = (DynamicThreadPoolExecutor) executor;
        Integer rejectCount = dynamicThreadPoolExecutor.getRejectCount();
        log.info("ThreadPool name :: {}, Reject count :: {}", Thread.currentThread().getName(), rejectCount);
    }
}
