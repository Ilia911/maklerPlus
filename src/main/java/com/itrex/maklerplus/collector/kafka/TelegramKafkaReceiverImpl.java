package com.itrex.maklerplus.collector.kafka;

import com.itrex.maklerplus.collector.exception.ServiceException;
import com.itrex.maklerplus.collector.service.AdvertService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramKafkaReceiverImpl implements KafkaReceiver {

  private final AdvertService advertService;

  @Override
  @KafkaListener(topics = "${topic.telegram.receive}")
  public void receiveMessage(String messages) throws ServiceException {
    advertService.processMessagesFromKafka(messages);
  }
}
