package cn.hippo4j.adapter.rocketmq;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * RocketMQ thread-pool adapter.
 */
@Slf4j
public class RocketMQThreadPoolAdapter implements ThreadPoolAdapter, ApplicationListener<ApplicationStartedEvent> {

    @Override
    public String mark() {
        return "RocketMQ";
    }

    @Override
    public ThreadPoolAdapterState getThreadPoolState(String identify) {
        return null;
    }

    @Override
    public boolean updateThreadPool(ThreadPoolAdapterParameter threadPoolAdapterParameter) {
        return false;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

    }
}
