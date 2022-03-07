package cn.hippo4j.core.starter.refresher;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.starter.config.BootstrapCoreProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Nacos refresher handler.
 *
 * @author chen.ma
 * @date 2022/2/26 00:10
 */
@Slf4j
public class NacosRefresherHandler extends AbstractCoreThreadPoolDynamicRefresh {

    private final ConfigService configService;

    public NacosRefresherHandler(ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler,
                                 BootstrapCoreProperties bootstrapCoreProperties) {
        super(threadPoolNotifyAlarmHandler, bootstrapCoreProperties);
        configService = ApplicationContextHolder.getBean(ConfigService.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> nacosConfig = bootstrapCoreProperties.getNacos();

        configService.addListener(nacosConfig.get("data-id"), nacosConfig.get("group"),
                new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return dynamicRefreshExecutorService;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        dynamicRefresh(configInfo);
                    }
                }
        );
    }

}
