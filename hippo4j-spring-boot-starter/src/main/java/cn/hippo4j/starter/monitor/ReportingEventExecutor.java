package cn.hippo4j.starter.monitor;

import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.common.monitor.AbstractMessage;
import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.MessageTypeEnum;
import cn.hippo4j.common.monitor.RuntimeMessage;
import cn.hippo4j.starter.config.BootstrapProperties;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.handler.AbstractThreadPoolRuntime;
import cn.hippo4j.starter.remote.ServerHealthCheck;
import cn.hippo4j.starter.toolkit.thread.ThreadFactoryBuilder;
import cn.hippo4j.starter.toolkit.thread.ThreadUtil;
import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.starter.core.GlobalThreadPoolManage.getThreadPoolNum;
import static cn.hippo4j.starter.toolkit.IdentifyUtil.getThreadPoolIdentify;

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
public class ReportingEventExecutor extends AbstractThreadPoolRuntime implements Runnable, Collect, CommandLineRunner, DisposableBean {

    @NonNull
    private final BootstrapProperties properties;

    @NonNull
    private final MessageSender messageSender;

    @NonNull
    private final ServerHealthCheck serverHealthCheck;

    /**
     * 数据采集的缓冲容器, 等待 ReportingEventExecutor 上报服务端
     */
    private final BlockingQueue<Message> messageCollectVessel = new ArrayBlockingQueue(4096);

    /**
     * 数据采集定时执行器, Spring 启动后延时一段时间进行采集动态线程池的运行数据
     */
    private final ScheduledThreadPoolExecutor collectVesselExecutor = new ScheduledThreadPoolExecutor(
            new Integer(1),
            ThreadFactoryBuilder.builder().daemon(true).prefix("collect-data-scheduled").build()
    );

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
        // 延迟 10秒后每 5秒调用一次. scheduleWithFixedDelay 每次执行时间为上一次任务结束时, 向后推一个时间间隔
        collectVesselExecutor.scheduleWithFixedDelay(() -> runTimeGatherTask(), 10, 5, TimeUnit.SECONDS);
        ThreadUtil.newThread(this, "reporting-task", Boolean.TRUE).start();

        log.info("Dynamic thread pool :: [{}]. The dynamic thread pool starts data collection and reporting. ", getThreadPoolNum());
    }

    @Override
    public void destroy() {
        Optional.ofNullable(collectVesselExecutor).ifPresent((each) -> each.shutdown());
    }

    /**
     * 采集动态线程池数据, 并添加缓冲队列
     */
    private void runTimeGatherTask() {
        serverHealthCheck.isHealthStatus();
        Message message = collectMessage();
        messageCollectVessel.offer(message);
    }

    @Override
    public Message collectMessage() {
        AbstractMessage message = new RuntimeMessage();

        List<Message> runtimeMessages = Lists.newArrayList();
        List<String> listThreadPoolId = GlobalThreadPoolManage.listThreadPoolId();
        for (String each : listThreadPoolId) {
            PoolRunStateInfo poolRunState = getPoolRunState(each);
            RuntimeMessage runtimeMessage = BeanUtil.toBean(poolRunState, RuntimeMessage.class);
            runtimeMessage.setGroupKey(getThreadPoolIdentify(each, properties));
            runtimeMessages.add(runtimeMessage);
        }

        message.setMessageType(MessageTypeEnum.RUNTIME);
        message.setMessages(runtimeMessages);

        return message;
    }

    @Override
    protected PoolRunStateInfo supplement(PoolRunStateInfo poolRunStateInfo) {
        return poolRunStateInfo;
    }

}
