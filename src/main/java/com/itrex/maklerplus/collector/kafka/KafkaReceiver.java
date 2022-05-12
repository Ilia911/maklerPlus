package com.itrex.maklerplus.collector.kafka;

import com.itrex.maklerplus.collector.exception.ServiceException;

public interface KafkaReceiver {

  void receiveMessage(String messages) throws ServiceException;
}
