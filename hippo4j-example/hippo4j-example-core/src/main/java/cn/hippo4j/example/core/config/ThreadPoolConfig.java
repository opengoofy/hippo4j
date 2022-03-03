package cn.hippo4j.example.core.config;

import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
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

    /**
     * {@link DynamicThreadPoolWrapper} 完成 Server 端订阅配置功能.
     *
     * @return
     */
    @Bean
    public DynamicThreadPoolWrapper messageCenterDynamicThreadPool() {
        ThreadPoolExecutor customExecutor = ThreadPoolBuilder.builder()
                .dynamicPool()
                .executeTimeOut(800L)
                .taskDecorator(new TaskTraceBuilderHandler())
                .threadFactory(MESSAGE_CONSUME)
                .build();

        return new DynamicThreadPoolWrapper(MESSAGE_CONSUME, customExecutor);
    }

    /**
     * 通过 {@link DynamicThreadPool} 修饰 {@link DynamicThreadPoolExecutor} 完成 Server 端订阅配置功能.
     * <p>
     * 由动态线程池注解修饰后, IOC 容器中保存的是 {@link DynamicThreadPoolExecutor}
     *
     * @return
     */
    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor dynamicThreadPoolExecutor() {
        return ThreadPoolBuilder.builder()
                .threadFactory(MESSAGE_PRODUCE)
                .dynamicPool()
                /**
                 * 测试线程任务装饰器.
                 * 如果需要查看详情, 跳转 {@link TaskDecoratorTest}
                 */
                .waitForTasksToCompleteOnShutdown(true)
                .awaitTerminationMillis(5000)
                .taskDecorator(new TaskDecoratorTest.ContextCopyingDecorator())
                .build();
    }

}
