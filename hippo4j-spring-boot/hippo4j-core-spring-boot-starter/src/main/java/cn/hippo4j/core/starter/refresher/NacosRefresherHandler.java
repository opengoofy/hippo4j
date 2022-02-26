package cn.hippo4j.core.starter.refresher;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Nacos refresher handler.
 *
 * @author chen.ma
 * @date 2022/2/26 00:10
 */
@Slf4j
public class NacosRefresherHandler implements InitializingBean, Listener {

    @Autowired(required = false)
    private ConfigService configService;

    @Override
    public void afterPropertiesSet() throws Exception {
        configService.addListener("hippo4j-nacos.yaml", "DEFAULT_GROUP", this);
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
