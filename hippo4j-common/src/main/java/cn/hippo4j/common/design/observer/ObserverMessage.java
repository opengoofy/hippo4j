package cn.hippo4j.common.design.observer;

/**
 * Message notifying observer.
 *
 * @author chen.ma
 * @date 2021/12/25 19:54
 */
public interface ObserverMessage<T> {

    /**
     * Message.
     *
     * @return
     */
    T message();

}
