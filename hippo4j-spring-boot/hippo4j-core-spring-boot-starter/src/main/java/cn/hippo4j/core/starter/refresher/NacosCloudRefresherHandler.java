package cn.hippo4j.core.starter.refresher;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.starter.config.BootstrapCoreProperties;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Nacos cloud refresher handler.
 *
 * @author chen.ma
 * @date 2022/2/26 11:21
 */
@Slf4j
public class NacosCloudRefresherHandler extends AbstractCoreThreadPoolDynamicRefresh {

    private final NacosConfigManager nacosConfigManager;

    public NacosCloudRefresherHandler(ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler,
                                      BootstrapCoreProperties bootstrapCoreProperties) {
        super(threadPoolNotifyAlarmHandler, bootstrapCoreProperties);
        nacosConfigManager = ApplicationContextHolder.getBean(NacosConfigManager.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> nacosConfig = bootstrapCoreProperties.getNacos();

        nacosConfigManager.getConfigService().addListener(nacosConfig.get("data-id"),
                nacosConfig.get("group"), new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return dynamicRefreshExecutorService;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        dynamicRefresh(configInfo);
                    }
                });
    }

}
