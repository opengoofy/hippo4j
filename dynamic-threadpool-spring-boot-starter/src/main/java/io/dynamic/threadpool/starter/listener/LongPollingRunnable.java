package io.dynamic.threadpool.starter.listener;

/**
 * 长轮询执行
 *
 * @author chen.ma
 * @date 2021/6/20 18:37
 */
public class LongPollingRunnable implements Runnable {

    private final int taskId;

    public LongPollingRunnable(Integer taskId) {
        this.taskId = taskId;
    }

    @Override
    public void run() {

    }
}
