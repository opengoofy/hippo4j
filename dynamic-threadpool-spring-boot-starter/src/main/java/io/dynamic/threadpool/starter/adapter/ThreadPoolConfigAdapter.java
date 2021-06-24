package io.dynamic.threadpool.starter.adapter;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import io.dynamic.threadpool.common.config.ApplicationContextHolder;
import io.dynamic.threadpool.starter.operation.ThreadPoolOperation;
import io.dynamic.threadpool.starter.wrap.DynamicThreadPoolWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
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

    private ExecutorService executorService = new ThreadPoolExecutor(
            2,
            4,
            0,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue(1),
            new ThreadFactoryBuilder().setNamePrefix("threadPool-config").build(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

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
