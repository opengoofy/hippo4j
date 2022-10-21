package cn.hippo4j.core.plugin;

import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;

/**
 * Factory of {@link ThreadPoolPlugin}.
 *
 * @author huangchengxing
 */
public interface ThreadPoolPluginRegistrar {

    /**
     * Get id.
     *
     * @return id
     */
    String getId();

    /**
     * Create and register plugin for the specified thread-pool instance
     *
     * @param registry thread pool plugin registry
     * @param executor executor
     */
    void doRegister(ThreadPoolPluginRegistry registry, ExtensibleThreadPoolExecutor executor);

}
