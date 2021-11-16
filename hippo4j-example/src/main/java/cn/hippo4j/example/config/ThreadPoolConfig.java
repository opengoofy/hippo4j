package cn.hippo4j.example.config;

import cn.hippo4j.starter.core.DynamicThreadPool;
import cn.hippo4j.starter.core.DynamicThreadPoolExecutor;
import cn.hippo4j.starter.toolkit.thread.ThreadPoolBuilder;
import cn.hippo4j.starter.wrapper.DynamicThreadPoolWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.example.constant.GlobalTestConstant.*;

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
        return new DynamicThreadPoolWrapper(MESSAGE_CONSUME);
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
        return ThreadPoolBuilder.builder().threadFactory(MESSAGE_PRODUCE).dynamicPool().build();
    }

}
