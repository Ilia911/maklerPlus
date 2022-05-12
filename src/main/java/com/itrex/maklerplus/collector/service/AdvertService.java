package com.itrex.maklerplus.collector.service;

import com.itrex.maklerplus.collector.exception.ServiceException;

public interface AdvertService {

  void processMessagesFromKafka(String message) throws ServiceException;
}
