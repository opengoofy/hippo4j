package cn.hippo4j.starter.event;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import javax.annotation.Resource;

/**
 * Application content post processor.
 *
 * @author chen.ma
 * @date 2021/12/25 20:21
 */
public class ApplicationContentPostProcessor implements ApplicationListener<ApplicationReadyEvent> {

    @Resource
    private ApplicationContext applicationContext;

    private boolean executeOnlyOnce = true;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        synchronized (ApplicationContentPostProcessor.class) {
            if (executeOnlyOnce) {
                applicationContext.publishEvent(new ApplicationCompleteEvent(this));
                executeOnlyOnce = false;
            }
        }
    }

}
