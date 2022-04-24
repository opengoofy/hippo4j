package cn.hippo4j.starter.event;

import org.springframework.context.ApplicationEvent;

/**
 * Execute after the spring application context is successfully started.
 *
 * @author chen.ma
 * @date 2021/12/25 21:19
 */
public class ApplicationCompleteEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public ApplicationCompleteEvent(Object source) {
        super(source);
    }

}
