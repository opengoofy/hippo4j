package cn.hippo4j.server.listener;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * Hippo4J application listener.
 *
 * @author chen.ma
 * @date 2022/2/9 04:35
 */
public interface Hippo4JApplicationListener {

    /**
     * {@link BaseSpringApplicationRunListener#starting}
     */
    void starting();

    /**
     * {@link BaseSpringApplicationRunListener#contextPrepared}
     *
     * @param context
     */
    void contextPrepared(ConfigurableApplicationContext context);

    /**
     * {@link BaseSpringApplicationRunListener#started}
     *
     * @param context context
     */
    void started(ConfigurableApplicationContext context);

    /**
     * {@link BaseSpringApplicationRunListener#failed}
     *
     * @param context
     * @param exception
     */
    void failed(ConfigurableApplicationContext context, Throwable exception);

}
