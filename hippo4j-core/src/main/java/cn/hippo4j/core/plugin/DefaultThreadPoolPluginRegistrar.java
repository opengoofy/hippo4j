package cn.hippo4j.core.plugin;

import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.AliasRegistry;

import java.util.Objects;

/**
 * Register default {@link ThreadPoolPlugin}.
 *
 * @author huangchengxing
 * @see TaskDecoratorPlugin
 * @see TaskTimeoutNotifyAlarmPlugin
 * @see TaskRejectCountRecordPlugin
 * @see TaskRejectNotifyAlarmPlugin
 * @see ThreadPoolExecutorShutdownPlugin
 */
@RequiredArgsConstructor
public class DefaultThreadPoolPluginRegistrar
    implements ThreadPoolPluginRegistrar, ApplicationContextAware, BeanNameAware {

    public static final String REGISTRAR_NAME = "DefaultThreadPoolPluginRegistrar";

    /**
     * aliasRegistry
     */
    private AliasRegistry aliasRegistry;

    /**
     * execute time out
     */
    private final long executeTimeOut;

    /**
     * await termination millis
     */
    private final long awaitTerminationMillis;

    /**
     * wait for tasks to complete on shutdown
     */
    private final boolean waitForTasksToCompleteOnShutdown;

    /**
     * Get id.
     *
     * @return id
     */
    @Override
    public String getId() {
        return REGISTRAR_NAME;
    }

    /**
     * Create and register plugin for the specified thread-pool instance
     *
     * @param registry thread pool plugin registry
     * @param executor executor
     */
    @Override
    public void doRegister(ThreadPoolPluginRegistry registry, ExtensibleThreadPoolExecutor executor) {
        // callback when task execute
        registry.register(new TaskDecoratorPlugin());
        registry.register(new TaskTimeoutNotifyAlarmPlugin(executeTimeOut, executor));
        // callback when task rejected
        registry.register(new TaskRejectCountRecordPlugin());
        registry.register(new TaskRejectNotifyAlarmPlugin());
        // callback when pool shutdown
        registry.register(new ThreadPoolExecutorShutdownPlugin(awaitTerminationMillis, waitForTasksToCompleteOnShutdown));
    }

    /**
     * Set the name of the bean in the bean factory that created this bean.
     * <p>Invoked after population of normal bean properties but before an
     * init callback such as {@link InitializingBean#afterPropertiesSet()}
     * or a custom init-method.
     *
     * @param name the name of the bean in the factory.
     *             Note that this name is the actual bean name used in the factory, which may
     *             differ from the originally specified name: in particular for inner bean
     *             names, the actual bean name might have been made unique through appending
     *             "#..." suffixes. Use the {@link BeanFactoryUtils#originalBeanName(String)}
     *             method to extract the original bean name (without suffix), if desired.
     */
    @Override
    public void setBeanName(String name) {
        if (Objects.nonNull(aliasRegistry)) {
            aliasRegistry.registerAlias(name, getId());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.aliasRegistry = applicationContext.getBean(AliasRegistry.class);
    }

}
