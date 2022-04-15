package com.itrex.maklerplus.collector.repository;

import com.itrex.maklerplus.collector.entity.Advert;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdvertRepository extends MongoRepository<Advert, String> {

}
