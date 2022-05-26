package com.itrex.maklerplus.collector.repository;

import com.itrex.maklerplus.collector.entity.AdvertMessage;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdvertRepository extends MongoRepository<AdvertMessage, String> {

  List<AdvertMessage> findByChatIdAndHostAPIEnumAndNativeIdIn(
      String chatId, String hostAPIEnum, List<String> nativeId);
}
