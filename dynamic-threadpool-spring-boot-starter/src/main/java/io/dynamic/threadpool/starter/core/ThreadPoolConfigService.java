package io.dynamic.threadpool.starter.core;

import io.dynamic.threadpool.starter.listener.ClientWorker;
import io.dynamic.threadpool.starter.listener.Listener;

import java.util.Arrays;

/**
 * 线程池配置服务
 *
 * @author chen.ma
 * @date 2021/6/21 21:50
 */
public class ThreadPoolConfigService implements ConfigService {

    private final ClientWorker clientWorker;

    public ThreadPoolConfigService() {
        clientWorker = new ClientWorker();
    }

    @Override
    public void addListener(String tpId, Listener listener) {
        clientWorker.addTenantListeners(tpId, Arrays.asList(listener));
    }
}
