package cn.hippo4j.starter.core;

import cn.hippo4j.starter.event.ApplicationCompleteEvent;
import cn.hippo4j.starter.remote.HttpAgent;
import cn.hippo4j.starter.remote.ServerHealthCheck;
import org.springframework.context.ApplicationListener;

import java.util.Arrays;

/**
 * ThreadPool config service.
 *
 * @author chen.ma
 * @date 2021/6/21 21:50
 */
public class ThreadPoolConfigService implements ConfigService, ApplicationListener<ApplicationCompleteEvent> {

    private final ClientWorker clientWorker;

    private final ServerHealthCheck serverHealthCheck;

    public ThreadPoolConfigService(HttpAgent httpAgent, String identification, ServerHealthCheck serverHealthCheck) {
        this.serverHealthCheck = serverHealthCheck;
        this.clientWorker = new ClientWorker(httpAgent, identification, serverHealthCheck);
    }

    @Override
    public void addListener(String tenantId, String itemId, String tpId, Listener listener) {
        clientWorker.addTenantListeners(tenantId, itemId, tpId, Arrays.asList(listener));
    }

    @Override
    public String getServerStatus() {
        if (serverHealthCheck.isHealthStatus()) {
            return "UP";
        } else {
            return "DOWN";
        }
    }

    @Override
    public void onApplicationEvent(ApplicationCompleteEvent event) {
        clientWorker.notifyApplicationComplete();
    }

}
