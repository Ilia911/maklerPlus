package com.itrex.maklerplus.collector.kafka;

import com.itrex.maklerplus.collector.exception.ServiceException;
import com.itrex.maklerplus.collector.service.AdvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaReceiver {

  private final AdvertService advertService;

  @KafkaListener(topics = "${topic.telegram.receive}")
  public void receiveMessagesFromTelegram(String messages) throws ServiceException {
    advertService.processMessagesFromKafka(messages);
  }
}
