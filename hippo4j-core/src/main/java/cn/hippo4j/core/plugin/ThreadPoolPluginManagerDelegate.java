package cn.hippo4j.core.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread pool plugin manager delegate.
 *
 * @author huangchengxing
 */
public interface ThreadPoolPluginManagerDelegate extends ThreadPoolPluginManager {

    /**
     * Get thread pool action aware registry.
     *
     * @return {@link ThreadPoolPluginManager}
     */
    @NonNull
    ThreadPoolPluginManager getThreadPoolPluginManager();

    /**
     * Get thread-pool id
     *
     * @return thread-pool id
     */
    String getThreadPoolId();

    /**
     * Get thread-pool executor.
     *
     * @return thread-pool executor
     */
    ThreadPoolExecutor getThreadPoolExecutor();

    // ======================== delegate methods ========================

    /**
     * Clear all.
     */
    @Override
    default void clear() {
        getThreadPoolPluginManager().clear();
    }

    /**
     * Register a {@link ThreadPoolPlugin}
     *
     * @param plugin aware
     */
    @Override
    default void register(ThreadPoolPlugin plugin) {
        getThreadPoolPluginManager().register(plugin);
    }

    /**
     * Whether the {@link ThreadPoolPlugin} has been registered.
     *
     * @param pluginId name
     * @return ture if target has been registered, false otherwise
     */
    @Override
    default boolean isRegistered(String pluginId) {
        return getThreadPoolPluginManager().isRegistered(pluginId);
    }

    /**
     * Unregister {@link ThreadPoolPlugin}
     *
     * @param pluginId name
     */
    @Override
    default void unregister(String pluginId) {
        getThreadPoolPluginManager().unregister(pluginId);
    }

    /**
     * Get all registered plugins.
     *
     * @return plugins
     */
    @Override
    default Collection<ThreadPoolPlugin> getAllPlugins() {
        return getThreadPoolPluginManager().getAllPlugins();
    }

    /**
     * Get {@link ThreadPoolPlugin}
     *
     * @param pluginId target name
     * @return {@link ThreadPoolPlugin}, null if unregister
     * @throws ClassCastException thrown when the object obtained by name cannot be converted to target type
     */
    @Override
    default <A extends ThreadPoolPlugin> Optional<A> getPlugin(String pluginId) {
        return getThreadPoolPluginManager().getPlugin(pluginId);
    }

    /**
     * Get execute aware list.
     *
     * @return {@link ExecuteAwarePlugin}
     */
    @Override
    default Collection<ExecuteAwarePlugin> getExecuteAwarePluginList() {
        return getThreadPoolPluginManager().getExecuteAwarePluginList();
    }

    /**
     * Get rejected aware list.
     *
     * @return {@link RejectedAwarePlugin}
     */
    @Override
    default Collection<RejectedAwarePlugin> getRejectedAwarePluginList() {
        return getThreadPoolPluginManager().getRejectedAwarePluginList();
    }

    /**
     * Get shutdown aware list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    @Override
    default Collection<ShutdownAwarePlugin> getShutdownAwarePluginList() {
        return getThreadPoolPluginManager().getShutdownAwarePluginList();
    }

    /**
     * Get shutdown aware list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    @Override
    default Collection<TaskAwarePlugin> getTaskAwarePluginList() {
        return getThreadPoolPluginManager().getTaskAwarePluginList();
    }

}
