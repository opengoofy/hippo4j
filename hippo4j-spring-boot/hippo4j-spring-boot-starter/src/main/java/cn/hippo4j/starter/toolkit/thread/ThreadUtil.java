package cn.hippo4j.starter.toolkit.thread;

/**
 * Thread util.
 *
 * @author chen.ma
 * @date 2021/12/6 23:34
 */
public class ThreadUtil {

    /**
     * 创建新线程.
     *
     * @param runnable {@link Runnable}
     * @param name     线程名
     * @param isDaemon 是否守护线程
     * @return {@link Thread}
     */
    public static Thread newThread(Runnable runnable, String name, boolean isDaemon) {
        Thread t = new Thread(null, runnable, name);
        t.setDaemon(isDaemon);
        return t;
    }

    /**
     * 挂起当前线程.
     *
     * @param millis 毫秒
     * @return
     */
    public static boolean sleep(long millis) {
        if (millis > 0) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                return false;
            }
        }

        return true;
    }

}
