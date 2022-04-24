package cn.hippo4j.config.event;

/**
 * Local data change event.
 *
 * @author chen.ma
 * @date 2021/6/23 19:13
 */
public class LocalDataChangeEvent extends AbstractEvent {

    /**
     * 租户+项目+线程池
     */
    public final String groupKey;

    /**
     * 客户端实例唯一标识
     */
    public final String identify;

    public LocalDataChangeEvent(String identify, String groupKey) {
        this.identify = identify;
        this.groupKey = groupKey;
    }

}
