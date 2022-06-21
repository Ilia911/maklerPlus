package com.itrex.maklerplus.collector.mongock;

import com.itrex.maklerplus.collector.entity.AdvertMessage;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@ChangeUnit(id = "001", order = "001", author = "Ilya_Eremkin")
@RequiredArgsConstructor
public class MongodbInitMigration {

  private final MongoTemplate mongoTemplate;

  @Execution
  public void addIndex() {
    mongoTemplate
        .indexOps(AdvertMessage.class)
        .ensureIndex(
            new Index()
                .on("chatId", Direction.ASC)
                .on("nativeId", Direction.ASC)
                .on("hostAPIEnum", Direction.ASC));
  }

  @RollbackExecution
  public void rollback() {}
}
