package cn.hippo4j.core.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;

/**
 * Thread pool action aware registry delegate.
 *
 * @author huangchengxing
 */
public interface ThreadPoolPluginRegistryDelegate extends ThreadPoolPluginRegistry {

    /**
     * Get thread pool action aware registry.
     *
     * @return {@link ThreadPoolPluginRegistry}
     */
    @NonNull
    ThreadPoolPluginRegistry getThreadPoolPluginRegistry();

    /**
     * Clear all.
     */
    @Override
    default void clear() {
        getThreadPoolPluginRegistry().clear();
    }

    /**
     * Register a {@link ThreadPoolPlugin}
     *
     * @param plugin aware
     */
    @Override
    default void register(ThreadPoolPlugin plugin) {
        getThreadPoolPluginRegistry().register(plugin);
    }

    /**
     * Whether the {@link ThreadPoolPlugin} has been registered.
     *
     * @param pluginId name
     * @return ture if target has been registered, false otherwise
     */
    @Override
    default boolean isRegistered(String pluginId) {
        return getThreadPoolPluginRegistry().isRegistered(pluginId);
    }

    /**
     * Unregister {@link ThreadPoolPlugin}
     *
     * @param pluginId name
     */
    @Override
    default void unregister(String pluginId) {
        getThreadPoolPluginRegistry().unregister(pluginId);
    }

    /**
     * Get all registered plugins.
     *
     * @return plugins
     */
    @Override
    default Collection<ThreadPoolPlugin> getAllPlugins() {
        return getThreadPoolPluginRegistry().getAllPlugins();
    }

    /**
     * Get {@link ThreadPoolPlugin}
     *
     * @param pluginId target name
     * @return {@link ThreadPoolPlugin}, null if unregister
     * @throws ClassCastException thrown when the object obtained by name cannot be converted to target type
     */
    @Nullable
    @Override
    default <A extends ThreadPoolPlugin> A getPlugin(String pluginId) {
        return getThreadPoolPluginRegistry().getPlugin(pluginId);
    }

    /**
     * Get execute aware list.
     *
     * @return {@link ExecuteAwarePlugin}
     */
    @Override
    default Collection<ExecuteAwarePlugin> getExecuteAwareList() {
        return getThreadPoolPluginRegistry().getExecuteAwareList();
    }

    /**
     * Get rejected aware list.
     *
     * @return {@link RejectedAwarePlugin}
     */
    @Override
    default Collection<RejectedAwarePlugin> getRejectedAwareList() {
        return getThreadPoolPluginRegistry().getRejectedAwareList();
    }

    /**
     * Get shutdown aware list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    @Override
    default Collection<ShutdownAwarePlugin> getShutdownAwareList() {
        return getThreadPoolPluginRegistry().getShutdownAwareList();
    }

    /**
     * Get shutdown aware list.
     *
     * @return {@link ShutdownAwarePlugin}
     */
    @Override
    default Collection<TaskAwarePlugin> getTaskAwareList() {
        return getThreadPoolPluginRegistry().getTaskAwareList();
    }
}
