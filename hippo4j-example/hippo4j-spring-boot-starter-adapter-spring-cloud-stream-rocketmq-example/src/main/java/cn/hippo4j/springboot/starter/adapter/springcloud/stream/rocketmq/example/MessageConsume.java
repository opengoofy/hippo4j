package cn.hippo4j.springboot.starter.adapter.springcloud.stream.rocketmq.example;

import cn.hippo4j.example.core.dto.SendMessageDTO;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Message consume.
 */
@Slf4j
@Component
public class MessageConsume {

    @StreamListener(MySink.INPUT)
    public void consumeMessage(@Payload SendMessageDTO message, @Headers Map headers) {
        long startTime = System.currentTimeMillis();
        try {
            // ignore
        } finally {
            log.info("Keys: {}, Msg id: {}, Execute time: {} ms, Message: {}", headers.get("rocketmq_KEYS"), headers.get("rocketmq_MESSAGE_ID"), System.currentTimeMillis() - startTime, JSON.toJSONString(message));
        }
        log.info("Input current thread name: {}", Thread.currentThread().getName());
    }

    @StreamListener(MySink.INPUT2)
    public void consumeSaveMessage(@Payload SendMessageDTO message, @Headers Map headers) {
        long startTime = System.currentTimeMillis();
        try {
            // ignore
        } finally {
            log.info("Keys: {}, Msg id: {}, Execute time: {} ms, Message: {}", headers.get("rocketmq_KEYS"), headers.get("rocketmq_MESSAGE_ID"), System.currentTimeMillis() - startTime, JSON.toJSONString(message));
        }
        log.info("Input2 current thread name: {}", Thread.currentThread().getName());
    }
}
