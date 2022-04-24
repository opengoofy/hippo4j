package cn.hippo4j.core.starter.refresher;

import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.starter.config.BootstrapCoreProperties;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.core.enums.ConfigFileFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author : wh
 * @date : 2022/2/28 21:32
 * @description:
 */
@Slf4j
public class ApolloRefresherHandler extends AbstractCoreThreadPoolDynamicRefresh {

    private static final String APOLLO_PROPERTY = "${spring.dynamic.thread-pool.apollo.namespace:application}";

    @Value(APOLLO_PROPERTY)
    private String namespace;

    public ApolloRefresherHandler(ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler, BootstrapCoreProperties bootstrapCoreProperties) {
        super(threadPoolNotifyAlarmHandler, bootstrapCoreProperties);
    }

    @Override
    public void afterPropertiesSet() {
        String[] apolloNamespaces = this.namespace.split(",");
        this.namespace = apolloNamespaces[0];
        Config config = ConfigService.getConfig(namespace);

        ConfigChangeListener configChangeListener = configChangeEvent -> {
            ConfigFile configFile = ConfigService.getConfigFile(
                    this.namespace.replaceAll("." + bootstrapCoreProperties.getConfigFileType().getValue(), ""),
                    ConfigFileFormat.fromString(bootstrapCoreProperties.getConfigFileType().getValue())
            );

            String configInfo = configFile.getContent();
            dynamicRefresh(configInfo);
        };

        config.addChangeListener(configChangeListener);
        log.info("dynamic-thread-pool refresher, add apollo listener success, namespace: {}", namespace);
    }

}
