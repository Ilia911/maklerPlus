package com.itrex.maklerplus.collector.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class KafkaProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  public void send(String topic, String payload) {
    log.info("sending payload='{}' to topic='{}'", payload, topic);
    kafkaTemplate.send(topic, payload);
  }
}
