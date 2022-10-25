package cn.hippo4j.core.plugin;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.impl.TaskDecoratorPlugin;
import cn.hippo4j.core.plugin.impl.TaskRejectCountRecordPlugin;
import cn.hippo4j.core.plugin.impl.TaskTimeRecordPlugin;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * test {@link ThreadPoolPlugin}'s info to json
 *
 * @author huangchengxing
 */
public class PluginInfoToJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicInteger taskExecuteCount = new AtomicInteger(0);

    @SneakyThrows
    @Test
    public void testToJson() {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginRegistry(),
            1, 1, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy()
        );

        // TaskDecoratorPlugin
        TaskDecoratorPlugin taskDecoratorPlugin = new TaskDecoratorPlugin();
        taskDecoratorPlugin.addDecorator(runnable -> () -> {
            taskExecuteCount.incrementAndGet();
            runnable.run();
        });
        taskDecoratorPlugin.addDecorator(runnable -> () -> {
            taskExecuteCount.incrementAndGet();
            runnable.run();
        });

        // TaskRejectCountRecordPlugin
        TaskRejectCountRecordPlugin taskRejectCountRecordPlugin = new TaskRejectCountRecordPlugin();
        executor.register(taskRejectCountRecordPlugin);

        // TaskRejectCountRecordPlugin
        TaskTimeRecordPlugin taskTimeRecordPlugin = new TaskTimeRecordPlugin();
        executor.register(taskTimeRecordPlugin);

        executor.submit(() -> ThreadUtil.sleep(100L));
        executor.submit(() -> ThreadUtil.sleep(300L));
        executor.submit(() -> ThreadUtil.sleep(200L));

        ThreadUtil.sleep(1000L);
        System.out.println(objectMapper.writeValueAsString(executor.getThreadPoolPluginRegistry().getAllPlugins()));
    }

}
