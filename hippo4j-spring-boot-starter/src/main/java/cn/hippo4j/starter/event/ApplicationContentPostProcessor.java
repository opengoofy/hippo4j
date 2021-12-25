package cn.hippo4j.starter.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Application content post processor.
 *
 * @author chen.ma
 * @date 2021/12/25 20:21
 */
public class ApplicationContentPostProcessor implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private ApplicationContext applicationContext;

    private AtomicBoolean executeOnlyOnce = new AtomicBoolean(Boolean.TRUE);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null && executeOnlyOnce.get()) {
            applicationContext.publishEvent(new ApplicationCompleteEvent(this));
            executeOnlyOnce.set(Boolean.FALSE);
        }
    }

}
