package io.dynamic.threadpool.starter.adapter;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import io.dynamic.threadpool.starter.operation.ThreadPoolOperation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
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

    public void subscribeConfig(List<String> tpIds) {
        tpIds.forEach(each -> threadPoolOperation.subscribeConfig(each, executorService, config -> callbackConfig(config)));
    }

}
