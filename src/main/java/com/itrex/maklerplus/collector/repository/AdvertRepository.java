package com.itrex.maklerplus.collector.repository;

import com.itrex.maklerplus.collector.entity.AdvertMessage;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AdvertRepository extends MongoRepository<AdvertMessage, String> {

  @Query("{status : ?0, createdTime : {$lt : ?1}}")
  List<AdvertMessage> findByStatusAndCreatedTime(String status, long createdTime);

  List<AdvertMessage> findByChatIdAndHostAPIEnumAndNativeIdIn(
      String chatId, String hostAPIEnum, List<String> nativeId);
}
