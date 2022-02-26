package cn.hippo4j.core.starter.refresher;

import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.starter.config.BootstrapCoreProperties;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Nacos refresher handler.
 *
 * @author chen.ma
 * @date 2022/2/26 00:10
 */
@Slf4j
public class NacosRefresherHandler extends AbstractCoreThreadPoolDynamicRefresh implements InitializingBean, Listener {

    private final ConfigService configService;

    public NacosRefresherHandler(ConfigService configService,
                                 ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler,
                                 ConfigParserHandler configParserHandler,
                                 BootstrapCoreProperties bootstrapCoreProperties) {
        super(threadPoolNotifyAlarmHandler, configParserHandler, bootstrapCoreProperties);
        this.configService = configService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> nacosConfig = bootstrapCoreProperties.getNacos();
        configService.addListener(nacosConfig.get("data-id"), nacosConfig.get("group"), this);
    }

    @Override
    public Executor getExecutor() {
        return dynamicRefreshExecutorService;
    }

    @Override
    public void receiveConfigInfo(String configInfo) {
        dynamicRefresh(configInfo);
    }

}
