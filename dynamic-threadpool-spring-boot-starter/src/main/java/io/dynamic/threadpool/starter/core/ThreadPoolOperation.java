package io.dynamic.threadpool.starter.core;

import io.dynamic.threadpool.starter.config.DynamicThreadPoolProperties;

import java.util.concurrent.Executor;

/**
 * ThreadPoolOperation.
 *
 * @author chen.ma
 * @date 2021/6/22 20:25
 */
public class ThreadPoolOperation {

    private final ConfigService configService;

    private final DynamicThreadPoolProperties properties;

    public ThreadPoolOperation(DynamicThreadPoolProperties properties, ConfigService configService) {
        this.properties = properties;
        this.configService = configService;
    }

    public Listener subscribeConfig(String tpId, Executor executor, ThreadPoolSubscribeCallback threadPoolSubscribeCallback) {
        Listener configListener = new Listener() {
            @Override
            public void receiveConfigInfo(String config) {
                threadPoolSubscribeCallback.callback(config);
            }

            @Override
            public Executor getExecutor() {
                return executor;
            }
        };

        configService.addListener(properties.getNamespace(), properties.getItemId(), tpId, configListener);

        return configListener;
    }
}
