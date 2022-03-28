package cn.hippo4j.common.monitor;

import java.util.List;
import java.util.Map;

/**
 * Message request.
 *
 * @author chen.ma
 * @date 2021/12/10 21:17
 */
public interface MessageRequest<T extends Message> {

    /**
     * Get content params.
     *
     * @return
     */
    List<Map<String, Object>> getContentParams();

    /**
     * Get response class.
     *
     * @return
     */
    Class<T> getResponseClass();

    /**
     * Get message type.
     *
     * @return
     */
    MessageTypeEnum getMessageType();

}
