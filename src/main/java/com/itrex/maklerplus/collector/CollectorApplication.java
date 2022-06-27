package com.itrex.maklerplus.collector;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableMongock
public class CollectorApplication {

  public static void main(String[] args) {
    SpringApplication.run(CollectorApplication.class, args);
  }
}
