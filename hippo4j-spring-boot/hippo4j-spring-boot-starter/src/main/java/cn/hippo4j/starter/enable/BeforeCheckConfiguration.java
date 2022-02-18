package cn.hippo4j.starter.enable;

import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.starter.config.BootstrapProperties;
import cn.hippo4j.starter.core.ConfigEmptyException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Before check configuration.
 *
 * @author chen.ma
 * @date 2021/11/28 22:44
 */
@Configuration
@AllArgsConstructor
public class BeforeCheckConfiguration {

    @Bean
    public BeforeCheckConfiguration.BeforeCheck dynamicThreadPoolBeforeCheckBean(@Autowired(required = false) BootstrapProperties properties,
                                                                                 ConfigurableEnvironment environment) {
        if (properties != null && properties.getEnable()) {
            String username = properties.getUsername();
            if (StringUtil.isBlank(username)) {
                throw new ConfigEmptyException(
                        "Web server failed to start. The dynamic thread pool username is empty.",
                        "Please check whether the [spring.dynamic.thread-pool.username] configuration is empty or an empty string."
                );
            }

            String password = properties.getPassword();
            if (StringUtil.isBlank(password)) {
                throw new ConfigEmptyException(
                        "Web server failed to start. The dynamic thread pool password is empty.",
                        "Please check whether the [spring.dynamic.thread-pool.password] configuration is empty or an empty string."
                );
            }

            String namespace = properties.getNamespace();
            if (StringUtil.isBlank(namespace)) {
                throw new ConfigEmptyException(
                        "Web server failed to start. The dynamic thread pool namespace is empty.",
                        "Please check whether the [spring.dynamic.thread-pool.namespace] configuration is empty or an empty string."
                );
            }

            String itemId = properties.getItemId();
            if (StringUtil.isBlank(itemId)) {
                throw new ConfigEmptyException(
                        "Web server failed to start. The dynamic thread pool item id is empty.",
                        "Please check whether the [spring.dynamic.thread-pool.item-id] configuration is empty or an empty string."
                );
            }

            String serverAddr = properties.getServerAddr();
            if (StringUtil.isBlank(serverAddr)) {
                throw new ConfigEmptyException(
                        "Web server failed to start. The dynamic thread pool server addr is empty.",
                        "Please check whether the [spring.dynamic.thread-pool.server-addr] configuration is empty or an empty string."
                );
            }

            String applicationName = environment.getProperty("spring.application.name");
            if (StringUtil.isBlank(applicationName)) {
                throw new ConfigEmptyException(
                        "Web server failed to start. The dynamic thread pool application name is empty.",
                        "Please check whether the [spring.application.name] configuration is empty or an empty string."
                );
            }
        }

        return new BeforeCheckConfiguration.BeforeCheck();
    }

    public class BeforeCheck {

    }

}
