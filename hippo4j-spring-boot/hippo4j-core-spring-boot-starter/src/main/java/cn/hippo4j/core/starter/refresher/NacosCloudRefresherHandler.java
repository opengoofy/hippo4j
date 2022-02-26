package cn.hippo4j.core.starter.refresher;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Nacos cloud refresher handler.
 *
 * @author chen.ma
 * @date 2022/2/26 11:21
 */
@Slf4j
@AllArgsConstructor
public class NacosCloudRefresherHandler implements InitializingBean, Listener {

    private final NacosConfigManager nacosConfigManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        nacosConfigManager.getConfigService().addListener("hippo4j-nacos.yaml", "DEFAULT_GROUP", this);
    }

    @Override
    public Executor getExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Override
    public void receiveConfigInfo(String configInfo) {
        log.info("Config :: {}", configInfo);
    }

}
