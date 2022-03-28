package cn.hippo4j.common.monitor;

import java.io.Serializable;
import java.util.List;

/**
 * Abstract message monitoring interface.
 *
 * @author chen.ma
 * @date 2021/12/6 20:16
 */
public interface Message<T extends Message> extends Serializable {

    /**
     * Get groupKey.
     *
     * @return
     */
    String getGroupKey();

    /**
     * Get message type.
     *
     * @return
     */
    MessageTypeEnum getMessageType();

    /**
     * Get messages.
     *
     * @return
     */
    List<T> getMessages();

}
