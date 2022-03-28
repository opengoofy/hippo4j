package cn.hippo4j.example.core.config;

import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import cn.hippo4j.example.core.handler.TaskTraceBuilderHandler;
import cn.hippo4j.example.core.inittest.TaskDecoratorTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.example.core.constant.GlobalTestConstant.MESSAGE_CONSUME;
import static cn.hippo4j.example.core.constant.GlobalTestConstant.MESSAGE_PRODUCE;

/**
 * Thread pool config.
 *
 * @author chen.ma
 * @date 2021/6/20 17:16
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor messageConsumeDynamicThreadPool() {
        String threadPoolId = MESSAGE_CONSUME;
        ThreadPoolExecutor customExecutor = ThreadPoolBuilder.builder()
                .dynamicPool()
                .threadFactory(threadPoolId)
                .threadPoolId(threadPoolId)
                .executeTimeOut(800L)
                .waitForTasksToCompleteOnShutdown(true)
                .awaitTerminationMillis(5000L)
                .taskDecorator(new TaskTraceBuilderHandler())
                .build();

        return customExecutor;
    }

    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor messageProduceDynamicThreadPool() {
        String threadPoolId = MESSAGE_PRODUCE;
        ThreadPoolExecutor produceExecutor = ThreadPoolBuilder.builder()
                .dynamicPool()
                .threadFactory(threadPoolId)
                .threadPoolId(threadPoolId)
                .executeTimeOut(900L)
                .waitForTasksToCompleteOnShutdown(true)
                .awaitTerminationMillis(5000L)
                /**
                 * 测试线程任务装饰器.
                 * 如果需要查看详情, 跳转 {@link TaskDecoratorTest}
                 */
                .taskDecorator(new TaskDecoratorTest.ContextCopyingDecorator())
                .build();

        return produceExecutor;
    }

}
