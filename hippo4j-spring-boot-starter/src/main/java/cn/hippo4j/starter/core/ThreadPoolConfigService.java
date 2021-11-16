package cn.hippo4j.starter.core;

import cn.hippo4j.starter.remote.HttpAgent;

import java.util.Arrays;

/**
 * ThreadPool config service.
 *
 * @author chen.ma
 * @date 2021/6/21 21:50
 */
public class ThreadPoolConfigService implements ConfigService {

    private final ClientWorker clientWorker;

    public ThreadPoolConfigService(HttpAgent httpAgent, String identification) {
        clientWorker = new ClientWorker(httpAgent, identification);
    }

    @Override
    public void addListener(String tenantId, String itemId, String tpId, Listener listener) {
        clientWorker.addTenantListeners(tenantId, itemId, tpId, Arrays.asList(listener));
    }

    @Override
    public String getServerStatus() {
        if (clientWorker.isHealthServer()) {
            return "UP";
        } else {
            return "DOWN";
        }
    }

}
