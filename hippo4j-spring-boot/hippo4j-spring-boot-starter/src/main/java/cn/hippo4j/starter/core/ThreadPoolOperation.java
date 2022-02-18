package cn.hippo4j.starter.core;

import cn.hippo4j.starter.config.BootstrapProperties;

import java.util.concurrent.Executor;

/**
 * ThreadPool operation.
 *
 * @author chen.ma
 * @date 2021/6/22 20:25
 */
public class ThreadPoolOperation {

    private final ConfigService configService;

    private final BootstrapProperties properties;

    public ThreadPoolOperation(BootstrapProperties properties, ConfigService configService) {
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
