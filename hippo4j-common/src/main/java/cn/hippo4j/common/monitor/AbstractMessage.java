package cn.hippo4j.common.monitor;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Base message.
 *
 * @author chen.ma
 * @date 2021/12/7 20:31
 */
@Data
@NoArgsConstructor
public abstract class AbstractMessage implements Message {

    /**
     * groupKey: tenant + item + tpId + identify
     */
    private String groupKey;

    /**
     * messageTypeEnum
     */
    private MessageTypeEnum messageType;

    /**
     * message
     */
    private List<Message> messages;

}
