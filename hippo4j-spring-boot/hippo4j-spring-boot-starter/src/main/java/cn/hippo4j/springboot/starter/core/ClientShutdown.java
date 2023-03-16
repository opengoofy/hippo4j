package cn.hippo4j.springboot.starter.core;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

public class ClientShutdown {

    @Getter
    private volatile boolean prepareClose = false;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private final static Long TIME_OUT_SECOND = 5L;

    public void prepareDestroy() throws InterruptedException {
        prepareClose = true;
        countDownLatch.await(TIME_OUT_SECOND, TimeUnit.SECONDS);
    }

    public void countDown() {
        countDownLatch.countDown();
    }

}
