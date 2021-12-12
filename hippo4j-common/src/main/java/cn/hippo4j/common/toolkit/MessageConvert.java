package cn.hippo4j.common.toolkit;

import cn.hippo4j.common.monitor.AbstractMessage;
import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.monitor.MessageWrapper;
import cn.hutool.core.bean.BeanUtil;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Message convert.
 *
 * @author chen.ma
 * @date 2021/12/10 21:27
 */
public class MessageConvert {

    /**
     * {@link Message} to {@link MessageWrapper}.
     *
     * @param message
     * @return
     */
    public static MessageWrapper convert(Message message) {
        MessageWrapper wrapper = new MessageWrapper();
        wrapper.setResponseClass(message.getClass());
        wrapper.setMessageType(message.getMessageType());

        List<Map<String, Object>> messageMapList = new ArrayList();
        List<Message> messages = message.getMessages();
        messages.forEach(each -> messageMapList.add(BeanUtil.beanToMap(each)));

        wrapper.setContentParams(messageMapList);
        return wrapper;
    }

    /**
     * {@link MessageWrapper} to {@link Message}.
     *
     * @param messageWrapper
     * @return
     */
    @SneakyThrows
    public static Message convert(MessageWrapper messageWrapper) {
        AbstractMessage message = (AbstractMessage) messageWrapper.getResponseClass().newInstance();
        List<Map<String, Object>> contentParams = messageWrapper.getContentParams();

        List<Message> messages = new ArrayList();
        contentParams.forEach(each -> messages.add(BeanUtil.toBean(each, messageWrapper.getResponseClass())));

        message.setMessages(messages);
        message.setMessageType(messageWrapper.getMessageType());
        return message;
    }

}
