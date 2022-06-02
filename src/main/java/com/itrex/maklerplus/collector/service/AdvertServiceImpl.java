package com.itrex.maklerplus.collector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itrex.maklerplus.collector.entity.AdvertMessage;
import com.itrex.maklerplus.collector.entity.AdvertMessageDto;
import com.itrex.maklerplus.collector.entity.AdvertMessageResponseDto;
import com.itrex.maklerplus.collector.entity.StatusEnum;
import com.itrex.maklerplus.collector.exception.ServiceException;
import com.itrex.maklerplus.collector.kafka.KafkaProducer;
import com.itrex.maklerplus.collector.repository.AdvertRepository;
import com.itrex.maklerplus.collector.util.TelegramMessageParserUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
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

  private static final String SUCCESS_CODE = "SUCCESS";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${topic.final-preparator.send}")
  private String sendTopic;

  @Override
  @Transactional
  public void processMessagesFromKafka(String messages) throws ServiceException {

    List<AdvertMessage> advertMessageList = messageParser.createAdverts(messages);

    advertMessageList = filterDuplicateAdvertMessages(advertMessageList);

    if (advertMessageList.size() > 0) {
      List<AdvertMessage> savedAdvertMessages = advertRepository.saveAll(advertMessageList);
      log.info("Saved AdvertMessage entities: '{}'", savedAdvertMessages);

      for (AdvertMessage advertMessage : advertMessageList) {
        sendAdvertMessage(advertMessage);
      }
    }
  }

  @Override
  public void sendAdvertMessage(AdvertMessage advertMessage) throws ServiceException {
    try {
      AdvertMessageDto advertMessageDto = convertAdvertMessageIntoAdvertMessageDto(advertMessage);
      kafkaProducer.send(sendTopic, objectMapper.writeValueAsString(advertMessageDto));
    } catch (JsonProcessingException e) {
      throw new ServiceException(
          String.format("Sending AdvertMessage FAILED! AdvertMessage: %s", advertMessage), e);
    }
  }

  @Override
  @Transactional
  public void processFinalPreparatorResponse(String response) throws ServiceException {

    AdvertMessageResponseDto responseEntity;
    try {
      responseEntity = objectMapper.readValue(response, AdvertMessageResponseDto.class);
    } catch (JsonProcessingException e) {
      throw new ServiceException(
          String.format("Reading response FAILED! response: %s", response), e);
    }
    Optional<AdvertMessage> optional = advertRepository.findById(responseEntity.getId());

    if (optional.isPresent()) {
      updateAdvertMessage(responseEntity, optional.get());
      advertRepository.save(optional.get());
      log.info("Updated AdvertMessage: '{}'", optional.get());
    } else {
      log.error("AdvertMessage with id: '{}' does not exist", responseEntity.getId());
    }
  }

  private AdvertMessageDto convertAdvertMessageIntoAdvertMessageDto(AdvertMessage advertMessage) {
    return AdvertMessageDto.builder()
        .id(advertMessage.getId())
        .message(advertMessage.getText())
        .build();
  }

  private void updateAdvertMessage(
      AdvertMessageResponseDto responseDto, AdvertMessage advertMessage) {

    String status = responseDto.getRespCode();

    if (SUCCESS_CODE.equals(status)) {
      advertMessage.setStatus(StatusEnum.COMPLETED);
      advertMessage.setRespCode(status);
    } else {
      advertMessage.setStatus(StatusEnum.INVALID);
      advertMessage.setRespCode(status);
    }
  }

  private List<AdvertMessage> filterDuplicateAdvertMessages(List<AdvertMessage> advertMessageList) {

    if (advertMessageList.size() == 0) {
      return advertMessageList;
    }

    String hostApiEnum = advertMessageList.get(0).getHostAPIEnum().name();
    Map<String, List<AdvertMessage>> preparedDataForQuery =
        advertMessageList.stream().collect(Collectors.groupingBy(AdvertMessage::getChatId));
    List<AdvertMessage> advertMessagesFromDatabase =
        retrieveDataFromDatabase(hostApiEnum, preparedDataForQuery);
    log.info("Found existed AdvertMessages: '{}'", advertMessagesFromDatabase);

    // "chatId-nativeId" is a pair that help to check equality of new objects and ones retrieved
    // from database
    List<String> idsFromDatabase =
        advertMessagesFromDatabase.stream()
            .map(am -> am.getChatId() + "-" + am.getNativeId())
            .collect(Collectors.toList());
    return advertMessageList.stream()
        .filter(am -> !idsFromDatabase.contains(am.getChatId() + "-" + am.getNativeId()))
        .collect(Collectors.toList());
  }

  private List<AdvertMessage> retrieveDataFromDatabase(
      String hostApiEnum, Map<String, List<AdvertMessage>> preparedDataForQuery) {
    List<AdvertMessage> resultList = new ArrayList<>();
    for (Entry<String, List<AdvertMessage>> stringListEntry : preparedDataForQuery.entrySet()) {
      String key = stringListEntry.getKey();
      List<String> value =
          stringListEntry.getValue().stream()
              .map(AdvertMessage::getNativeId)
              .collect(Collectors.toList());
      resultList.addAll(
          advertRepository.findByChatIdAndHostAPIEnumAndNativeIdIn(key, hostApiEnum, value));
    }
    return resultList;
  }
}
