package com.itrex.maklerplus.collector.kafka;

import com.itrex.maklerplus.collector.exception.ServiceException;
import com.itrex.maklerplus.collector.service.AdvertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class KafkaReceiver {

  private final AdvertService advertService;

  @KafkaListener(topics = "${topic.telegram.receive}")
  public void receiveMessagesFromTelegram(String messages) throws ServiceException {
    log.info(
        "Received messages: '{}...' From topic: '{}'",
        messages.substring(0, 44),
        "${topic.telegram.receive}");
    advertService.processMessagesFromKafka(messages);
  }

  @KafkaListener(topics = "${topic.final-preparator.receive}")
  public void receiveResponseFromFinalPreparator(String response) throws ServiceException {
    log.info(
        "Received response: '{}'. From topic: '{}'", response, "${topic.final-preparator.receive}");
    advertService.processFinalPreparatorResponse(response);
  }
}
