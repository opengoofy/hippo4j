package cn.hippo4j.springboot.starter.adapter.kafka.example.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Kafka message consumer.
 */
@Slf4j
@Component
public class KafkaMessageConsumer {

    @KafkaListener(topics = "kafka_message_hippo4j", groupId = "hippo4j")
    public void onMessage(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        Optional message = Optional.ofNullable(record.value());
        message.ifPresent(each -> log.info(each.toString()));
        ack.acknowledge();
    }
}
