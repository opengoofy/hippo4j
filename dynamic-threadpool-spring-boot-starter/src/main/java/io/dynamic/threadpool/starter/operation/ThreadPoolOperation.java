package io.dynamic.threadpool.starter.operation;

import io.dynamic.threadpool.starter.core.ConfigService;
import io.dynamic.threadpool.starter.listener.Listener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Executor;

/**
 * ThreadPoolOperation.
 *
 * @author chen.ma
 * @date 2021/6/22 20:25
 */
public class ThreadPoolOperation {

    @Autowired
    private ConfigService configService;

    public Listener subscribeConfig(String tpId, Executor executor, ThreadPoolSubscribeCallback threadPoolSubscribeCallback) {
        Listener configListener = new Listener() {
            @Override
            public void receiveConfigInfo(String config) {
                threadPoolSubscribeCallback.callback(tpId, config);
            }

            @Override
            public Executor getExecutor() {
                return executor;
            }
        };

        configService.addListener(tpId, configListener);

        return configListener;
    }
}
