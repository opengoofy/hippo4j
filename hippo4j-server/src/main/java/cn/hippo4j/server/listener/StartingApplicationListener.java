package cn.hippo4j.server.listener;

import cn.hippo4j.config.toolkit.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Paths;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Starting application listener.
 *
 * @author chen.ma
 * @date 2022/2/9 04:39
 */
@Slf4j
public class StartingApplicationListener implements Hippo4JApplicationListener {

    private volatile boolean starting;

    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public void starting() {
        starting = true;
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        if (EnvUtil.getStandaloneMode()) {
            scheduledExecutorService = new ScheduledThreadPoolExecutor(
                    1,
                    r -> {
                        Thread thread = new Thread(r);
                        thread.setName("server.hippo4j-starting");
                        return thread;
                    }
            );

            scheduledExecutorService.scheduleWithFixedDelay(() -> {
                if (starting) {
                    log.info("Hippo4J is starting...");
                }
            }, 1, 1, TimeUnit.SECONDS);
        }
    }

    @Override
    public void started(ConfigurableApplicationContext context) {
        starting = false;

        closeExecutor();

        if (EnvUtil.getStandaloneMode()) {
            log.info("Hippo4J started successfully...");
        }
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        log.error("Startup errors : {}", exception);

        closeExecutor();
        context.close();

        log.error("Hippo4J failed to start, please see {} for more details.",
                Paths.get(EnvUtil.getHippo4JHome(), "logs/hippo4j.log"));
    }

    private void closeExecutor() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
        }
    }

}
