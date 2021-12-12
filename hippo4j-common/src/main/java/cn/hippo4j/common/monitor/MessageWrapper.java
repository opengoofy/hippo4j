package cn.hippo4j.common.monitor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Message wrapper.
 *
 * @author chen.ma
 * @date 2021/12/7 22:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageWrapper implements MessageRequest<Message> {

    /**
     * contentParams
     */
    private List<Map<String, Object>> contentParams;

    /**
     * responseClass
     */
    private Class responseClass;

    /**
     * getMessageType
     */
    private MessageTypeEnum messageType;

    @Override
    public List<Map<String, Object>> getContentParams() {
        return contentParams;
    }

    @Override
    public Class<Message> getResponseClass() {
        return responseClass;
    }

    @Override
    public MessageTypeEnum getMessageType() {
        return messageType;
    }

}
