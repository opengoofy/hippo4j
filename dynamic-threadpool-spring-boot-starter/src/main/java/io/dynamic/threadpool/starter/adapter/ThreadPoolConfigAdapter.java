package io.dynamic.threadpool.starter.adapter;

import io.dynamic.threadpool.common.config.ApplicationContextHolder;
import io.dynamic.threadpool.common.enums.QueueTypeEnum;
import io.dynamic.threadpool.starter.operation.ThreadPoolOperation;
import io.dynamic.threadpool.starter.toolkit.thread.ThreadPoolBuilder;
import io.dynamic.threadpool.starter.wrap.DynamicThreadPoolWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 动态线程池配置适配器
 *
 * @author chen.ma
 * @date 2021/6/22 20:17
 */
public class ThreadPoolConfigAdapter extends ConfigAdapter {

    @Autowired
    private ThreadPoolOperation threadPoolOperation;

    private ExecutorService executorService = ThreadPoolBuilder.builder()
            .poolThreadSize(2, 4)
            .keepAliveTime(0L, TimeUnit.MILLISECONDS)
            .workQueue(QueueTypeEnum.ARRAY_BLOCKING_QUEUE, 1)
            .threadFactory("threadPool-config")
            .rejected(new ThreadPoolExecutor.DiscardOldestPolicy())
            .build();

    @Order(1025)
    @PostConstruct
    public void subscribeConfig() {
        Map<String, DynamicThreadPoolWrap> executorMap =
                ApplicationContextHolder.getBeansOfType(DynamicThreadPoolWrap.class);

        List<String> tpIdList = new ArrayList();
        executorMap.forEach((key, val) -> tpIdList.add(val.getTpId()));

        tpIdList.forEach(each -> threadPoolOperation.subscribeConfig(each, executorService, config -> callbackConfig(config)));
    }

}
