package cn.hippo4j.starter.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;

/**
 * Application content post processor.
 *
 * @author chen.ma
 * @date 2021/12/25 20:21
 */
public class ApplicationContentPostProcessor implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private ApplicationContext applicationContext;

    private boolean executeOnlyOnce = true;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        synchronized (ApplicationContentPostProcessor.class) {
            if (executeOnlyOnce) {
                applicationContext.publishEvent(new ApplicationCompleteEvent(this));
                executeOnlyOnce = false;
            }
        }
    }

}
