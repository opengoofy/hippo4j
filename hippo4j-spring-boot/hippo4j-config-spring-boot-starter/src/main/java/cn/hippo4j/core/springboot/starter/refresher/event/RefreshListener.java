package cn.hippo4j.core.springboot.starter.refresher.event;

import cn.hippo4j.common.function.Matcher;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Refresh listener interface.
 * T：event.
 * M：properties.
 */
public interface RefreshListener<T extends ApplicationEvent, M> extends ApplicationListener<T>, Matcher<M> {

}
