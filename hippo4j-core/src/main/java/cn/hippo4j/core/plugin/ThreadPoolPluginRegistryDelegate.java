package cn.hippo4j.core.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;

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
     * @param aware aware
     */
    @Override
    default void register(ThreadPoolPlugin aware) {
        getThreadPoolPluginRegistry().register(aware);
    }

    /**
     * Whether the {@link ThreadPoolPlugin} has been registered.
     *
     * @param name name
     * @return ture if target has been registered, false otherwise
     */
    @Override
    default boolean isRegistered(String name) {
        return getThreadPoolPluginRegistry().isRegistered(name);
    }

    /**
     * Unregister {@link ThreadPoolPlugin}
     *
     * @param name name
     */
    @Override
    default void unregister(String name) {
        getThreadPoolPluginRegistry().unregister(name);
    }

    /**
     * Get {@link ThreadPoolPlugin}
     *
     * @param name target name
     * @return {@link ThreadPoolPlugin}, null if unregister
     * @throws ClassCastException thrown when the object obtained by name cannot be converted to target type
     */
    @Override
    default <A extends ThreadPoolPlugin> A getAware(String name) {
        return getThreadPoolPluginRegistry().getAware(name);
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
