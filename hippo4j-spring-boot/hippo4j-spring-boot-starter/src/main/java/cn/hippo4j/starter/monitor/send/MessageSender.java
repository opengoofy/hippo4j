package cn.hippo4j.starter.monitor.send;

import cn.hippo4j.common.monitor.Message;

/**
 * Message sender.
 *
 * @author chen.ma
 * @date 2021/12/7 20:49
 */
public interface MessageSender {

    /**
     * Send.
     *
     * @param message
     */
    void send(Message message);

}
