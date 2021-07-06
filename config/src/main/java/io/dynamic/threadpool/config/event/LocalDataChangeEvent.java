package io.dynamic.threadpool.config.event;

/**
 * Local Data Change Event.
 *
 * @author chen.ma
 * @date 2021/6/23 19:13
 */
public class LocalDataChangeEvent extends Event {

    public final String groupKey;

    public LocalDataChangeEvent(String groupKey) {
        this.groupKey = groupKey;
    }
}
