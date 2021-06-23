package io.dynamic.threadpool.server.notify;

/**
 * Slow Event.
 *
 * @author chen.ma
 * @date 2021/6/23 19:05
 */
public abstract class SlowEvent extends Event {
    @Override
    public long sequence() {
        return 0;
    }
}
