package com.itrex.maklerplus.collector.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdvertMessageDto {

  private String id;

  private String message;
}
