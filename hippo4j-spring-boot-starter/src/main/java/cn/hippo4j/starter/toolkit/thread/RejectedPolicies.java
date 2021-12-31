package cn.hippo4j.starter.toolkit.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Rejected policies.
 *
 * @author chen.ma
 * @date 2021/7/5 21:23
 */
@Slf4j
public class RejectedPolicies {

    /**
     * 发生拒绝事件时, 添加新任务并运行最早的任务.
     */
    public static class RunsOldestTaskPolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (executor.isShutdown()) {
                return;
            }
            BlockingQueue<Runnable> workQueue = executor.getQueue();
            Runnable firstWork = workQueue.poll();
            boolean newTaskAdd = workQueue.offer(r);
            if (firstWork != null) {
                firstWork.run();
            }
            if (!newTaskAdd) {
                executor.execute(r);
            }
        }

    }

    /**
     * 使用阻塞方法将拒绝任务添加队列, 可保证任务不丢失.
     */
    public static class SyncPutQueuePolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (executor.isShutdown()) {
                return;
            }
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                log.error("Adding Queue task to thread pool failed.", e);
            }
        }

    }

}
