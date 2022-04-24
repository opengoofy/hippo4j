package cn.hippo4j.server.listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;

/**
 * Base spring application run listener.
 *
 * @author chen.ma
 * @date 2022/2/9 04:29
 */
public class BaseSpringApplicationRunListener implements SpringApplicationRunListener, Ordered {

    private final SpringApplication application;

    private final String[] args;

    private List<Hippo4JApplicationListener> hippo4JApplicationListeners = new ArrayList();

    {
        hippo4JApplicationListeners.add(new StartingApplicationListener());
    }

    public BaseSpringApplicationRunListener(SpringApplication application, String[] args) {
        this.application = application;
        this.args = args;
    }

    @Override
    public void starting() {
        hippo4JApplicationListeners.forEach(each -> each.starting());
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        hippo4JApplicationListeners.forEach(each -> each.contextPrepared(context));
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        hippo4JApplicationListeners.forEach(each -> each.started(context));
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        hippo4JApplicationListeners.forEach(each -> each.failed(context, exception));
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
