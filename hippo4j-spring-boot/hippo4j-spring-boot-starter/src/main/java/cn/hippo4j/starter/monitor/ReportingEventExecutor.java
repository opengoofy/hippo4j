package cn.hippo4j.starter.monitor;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.starter.config.BootstrapProperties;
import cn.hippo4j.starter.monitor.collect.Collector;
import cn.hippo4j.starter.monitor.send.MessageSender;
import cn.hippo4j.starter.remote.ServerHealthCheck;
import cn.hippo4j.starter.toolkit.thread.ThreadFactoryBuilder;
import cn.hippo4j.starter.toolkit.thread.ThreadUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.starter.core.GlobalThreadPoolManage.getThreadPoolNum;

/**
 * 动态线程池采集上报事件执行器.
 * <p>
 * {@link BlockingQueue} 充当缓冲容器, 实现生产-消费模型.
 *
 * @author chen.ma
 * @date 2021/12/6 20:23
 */
@Slf4j
@RequiredArgsConstructor
public class ReportingEventExecutor implements Runnable, CommandLineRunner, DisposableBean {

    @NonNull
    private final BootstrapProperties properties;

    @NonNull
    private final MessageSender messageSender;

    @NonNull
    private final ServerHealthCheck serverHealthCheck;

    /**
     * 数据采集组件集合
     */
    private Map<String, Collector> collectors;

    /**
     * 数据采集的缓冲容器, 等待 ReportingEventExecutor 上报服务端
     */
    private BlockingQueue<Message> messageCollectVessel;

    /**
     * 数据采集定时执行器, Spring 启动后延时一段时间进行采集动态线程池的运行数据
     */
    private ScheduledThreadPoolExecutor collectVesselExecutor;

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            try {
                Message message = messageCollectVessel.take();
                messageSender.send(message);
            } catch (Throwable ex) {
                log.error("Consumption buffer container task failed. Number of buffer container tasks :: {}", messageCollectVessel.size(), ex);
            }
        }
    }

    @Override
    public void run(String... args) {
        if (properties.getEnableCollect()) {
            Integer bufferSize = properties.getTaskBufferSize();
            messageCollectVessel = new ArrayBlockingQueue(bufferSize);

            String collectVesselTaskName = "client.scheduled.collect.data";
            collectVesselExecutor = new ScheduledThreadPoolExecutor(
                    new Integer(1),
                    ThreadFactoryBuilder.builder().daemon(true).prefix(collectVesselTaskName).build()
            );

            // 延迟 initialDelay 后循环调用. scheduleWithFixedDelay 每次执行时间为上一次任务结束时, 向后推一个时间间隔
            collectVesselExecutor.scheduleWithFixedDelay(
                    () -> runTimeGatherTask(),
                    properties.getInitialDelay(),
                    properties.getCollectInterval(),
                    TimeUnit.MILLISECONDS
            );

            // 启动上报监控数据线程
            String reportingTaskName = "client.thread.reporting.task";
            ThreadUtil.newThread(this, reportingTaskName, Boolean.TRUE).start();

            // 获取所有数据采集组件, 目前仅有历史运行数据采集
            collectors = ApplicationContextHolder.getBeansOfType(Collector.class);
        }

        log.info("Dynamic thread pool :: [{}]. The dynamic thread pool starts data collection and reporting. ", getThreadPoolNum());
    }

    @Override
    public void destroy() {
        Optional.ofNullable(collectVesselExecutor).ifPresent((each) -> each.shutdown());
    }

    /**
     * 采集动态线程池数据, 并添加缓冲队列.
     */
    private void runTimeGatherTask() {
        boolean healthStatus = serverHealthCheck.isHealthStatus();
        if (!healthStatus || CollUtil.isEmpty(collectors)) {
            return;
        }

        collectors.forEach((beanName, collector) -> {
            Message message = collector.collectMessage();
            boolean offer = messageCollectVessel.offer(message);
            if (!offer) {
                log.warn("Buffer data starts stacking data...");
            }
        });
    }

}
