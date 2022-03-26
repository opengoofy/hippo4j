package cn.hippo4j.core.starter.monitor;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * Metric monitor handler.
 *
 * @author chen.ma
 * @date 2022/3/25 20:37
 */
public class MetricMonitorHandler extends AbstractDynamicThreadPoolMonitor {

    private final static String METRIC_NAME_PREFIX = "dynamic.thread-pool";

    private final static String DYNAMIC_THREAD_POOL_ID_TAG = METRIC_NAME_PREFIX + ".id";

    private final static String APPLICATION_NAME_TAG = "application.name";

    private final Map<String, PoolRunStateInfo> RUN_STATE_CACHE = Maps.newConcurrentMap();

    public MetricMonitorHandler(ThreadPoolRunStateHandler threadPoolRunStateHandler) {
        super(threadPoolRunStateHandler);
    }

    @Override
    protected void execute(PoolRunStateInfo poolRunStateInfo) {
        PoolRunStateInfo stateInfo = RUN_STATE_CACHE.get(poolRunStateInfo.getTpId());
        if (stateInfo == null) {
            RUN_STATE_CACHE.put(poolRunStateInfo.getTpId(), poolRunStateInfo);
        } else {
            BeanUtil.copyProperties(poolRunStateInfo, stateInfo);
        }

        Environment environment = ApplicationContextHolder.getInstance().getEnvironment();
        String applicationName = environment.getProperty("spring.application.name", "application");
        Iterable<Tag> tags = Lists.newArrayList(
                Tag.of(DYNAMIC_THREAD_POOL_ID_TAG, poolRunStateInfo.getTpId()),
                Tag.of(APPLICATION_NAME_TAG, applicationName)
        );

        // load
        Metrics.gauge(metricName("current.load"), tags, poolRunStateInfo, PoolRunStateInfo::getSimpleCurrentLoad);
        Metrics.gauge(metricName("peak.load"), tags, poolRunStateInfo, PoolRunStateInfo::getSimplePeakLoad);
        // thread pool
        Metrics.gauge(metricName("core.size"), tags, poolRunStateInfo, PoolRunStateInfo::getCoreSize);
        Metrics.gauge(metricName("maximum.size"), tags, poolRunStateInfo, PoolRunStateInfo::getMaximumSize);
        Metrics.gauge(metricName("current.size"), tags, poolRunStateInfo, PoolRunStateInfo::getPoolSize);
        Metrics.gauge(metricName("largest.size"), tags, poolRunStateInfo, PoolRunStateInfo::getLargestPoolSize);
        Metrics.gauge(metricName("active.size"), tags, poolRunStateInfo, PoolRunStateInfo::getActiveSize);
        // queue
        Metrics.gauge(metricName("queue.size"), tags, poolRunStateInfo, PoolRunStateInfo::getQueueSize);
        Metrics.gauge(metricName("queue.capacity"), tags, poolRunStateInfo, PoolRunStateInfo::getQueueCapacity);
        Metrics.gauge(metricName("queue.remaining.capacity"), tags, poolRunStateInfo, PoolRunStateInfo::getQueueRemainingCapacity);
        // other
        Metrics.gauge(metricName("completed.task.count"), tags, poolRunStateInfo, PoolRunStateInfo::getCompletedTaskCount);
        Metrics.gauge(metricName("reject.count"), tags, poolRunStateInfo, PoolRunStateInfo::getRejectCount);
    }

    private String metricName(String name) {
        return String.join(".", METRIC_NAME_PREFIX, name);
    }

    @Override
    public String getType() {
        return "metric";
    }

}
