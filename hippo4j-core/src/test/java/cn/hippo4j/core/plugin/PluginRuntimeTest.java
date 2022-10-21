package cn.hippo4j.core.plugin;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.impl.TaskRejectCountRecordPlugin;
import cn.hippo4j.core.plugin.impl.TaskTimeRecordPlugin;
import cn.hippo4j.core.plugin.impl.ThreadPoolExecutorShutdownPlugin;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * test {@link ThreadPoolPlugin}'s info to json
 *
 * @author huangchengxing
 */
public class PluginRuntimeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Test
    public void testGetPluginRuntime() {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginRegistry(),
            1, 1, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy()
        );

        // TaskRejectCountRecordPlugin
        TaskRejectCountRecordPlugin taskRejectCountRecordPlugin = new TaskRejectCountRecordPlugin();
        executor.register(taskRejectCountRecordPlugin);

        // TaskRejectCountRecordPlugin
        TaskTimeRecordPlugin taskTimeRecordPlugin = new TaskTimeRecordPlugin();
        executor.register(taskTimeRecordPlugin);

        // ThreadPoolExecutorShutdownPlugin
        ThreadPoolExecutorShutdownPlugin executorShutdownPlugin = new ThreadPoolExecutorShutdownPlugin(2000L, true);
        executor.register(executorShutdownPlugin);

        executor.submit(() -> ThreadUtil.sleep(100L));
        executor.submit(() -> ThreadUtil.sleep(300L));
        executor.submit(() -> ThreadUtil.sleep(200L));

        ThreadUtil.sleep(1000L);
        List<PluginRuntime> runtimeList = executor.getAllPlugins().stream()
            .map(ThreadPoolPlugin::getPluginRuntime)
            .collect(Collectors.toList());
        System.out.println(objectMapper.writeValueAsString(runtimeList));
    }

}
