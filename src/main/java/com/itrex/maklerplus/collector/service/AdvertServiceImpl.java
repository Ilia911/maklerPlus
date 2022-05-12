package com.itrex.maklerplus.collector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itrex.maklerplus.collector.entity.AdvertMessage;
import com.itrex.maklerplus.collector.exception.ServiceException;
import com.itrex.maklerplus.collector.kafka.KafkaProducer;
import com.itrex.maklerplus.collector.repository.AdvertRepository;
import com.itrex.maklerplus.collector.util.TelegramMessageParserUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class AdvertServiceImpl implements AdvertService {

  private final AdvertRepository advertRepository;
  private final TelegramMessageParserUtil messageParser;
  private final KafkaProducer kafkaProducer;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${topic.advert.send}")
  private String sendTopic;

  @Override
  @Transactional
  public void processMessagesFromKafka(String messages) throws ServiceException {

    List<AdvertMessage> advertMessageList = messageParser.createAdverts(messages);

    if (advertMessageList.size() > 0) {
      List<AdvertMessage> savedAdvertMessages = advertRepository.saveAll(advertMessageList);
      log.debug("Saved Adverts: {}", savedAdvertMessages);
      sendAdverts(advertMessageList);
    }
  }

  private void sendAdverts(List<AdvertMessage> advertMessageList) throws ServiceException {
    for (AdvertMessage advertMessage : advertMessageList) {
      try {
        kafkaProducer.send(sendTopic, objectMapper.writeValueAsString(advertMessage));
      } catch (JsonProcessingException e) {
        throw new ServiceException(e);
      }
    }
  }
}
