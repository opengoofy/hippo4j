package cn.hippo4j.common.design.observer;

/**
 * Observer.
 *
 * @author chen.ma
 * @date 2021/12/25 19:46
 */
public interface Observer {

    /**
     * Receive notification.
     *
     * @param observerMessage
     */
    void accept(ObserverMessage observerMessage);

}
