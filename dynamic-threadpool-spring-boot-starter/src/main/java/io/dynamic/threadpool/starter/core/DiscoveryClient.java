package io.dynamic.threadpool.starter.core;

/**
 * Discovery Client.
 *
 * @author chen.ma
 * @date 2021/7/13 21:51
 */
public class DiscoveryClient {

    private InstanceInfo instanceInfo;

    /**
     * 初始化所有计划任务
     */
    private void initScheduledTasks() {

    }

    /**
     * 注册实例到服务端
     *
     * @return
     */
    boolean register() {

        return true;
    }


    /**
     * 与 Server 端保持心跳续约
     */
    public class HeartbeatThread implements Runnable {

        @Override
        public void run() {

        }
    }

    /**
     * 心跳续约
     *
     * @return
     */
    boolean renew() {

        return true;
    }
}
