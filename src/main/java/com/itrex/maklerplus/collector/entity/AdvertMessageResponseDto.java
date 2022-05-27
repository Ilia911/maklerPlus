package com.itrex.maklerplus.collector.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdvertMessageResponseDto {

  private String id;

  private String respCode;
}
