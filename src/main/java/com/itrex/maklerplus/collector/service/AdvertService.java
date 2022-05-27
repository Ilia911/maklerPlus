package com.itrex.maklerplus.collector.service;

import com.itrex.maklerplus.collector.entity.AdvertMessage;
import com.itrex.maklerplus.collector.exception.ServiceException;

public interface AdvertService {

  void processMessagesFromKafka(String message) throws ServiceException;

  void sendAdvertMessage(AdvertMessage advertMessage) throws ServiceException;

  void processFinalPreparatorResponse(String response) throws ServiceException;
}
