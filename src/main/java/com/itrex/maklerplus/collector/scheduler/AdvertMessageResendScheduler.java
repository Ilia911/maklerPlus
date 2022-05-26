package com.itrex.maklerplus.collector.scheduler;

import com.itrex.maklerplus.collector.entity.AdvertMessage;
import com.itrex.maklerplus.collector.entity.StatusCodeEnum;
import com.itrex.maklerplus.collector.exception.ServiceException;
import com.itrex.maklerplus.collector.repository.AdvertRepository;
import com.itrex.maklerplus.collector.service.AdvertService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class AdvertMessageResendScheduler {

  @Value("${scheduler.resend.wait}")
  private int waitTime;

  @Value("${scheduler.resend.attempts.count}")
  private int maxAttemptsCount;

  private final AdvertRepository advertRepository;
  private final AdvertService advertService;

  @Scheduled(fixedRateString = "${scheduler.resend.delay}")
  @Transactional
  public void resendAndUpdateAdvertMessages() throws ServiceException {

    List<AdvertMessage> advertMessages =
        advertRepository.findByStatusCodeAndCreatedTime(
            StatusCodeEnum.IN_PROCESS.toString(), System.currentTimeMillis() - waitTime);

    for (AdvertMessage advertMessage : advertMessages) {
      if (advertMessage.getAttemptsCount() < maxAttemptsCount) {
        advertService.sendAdvertMessage(advertMessage);
        advertMessage.setAttemptsCount((byte) (advertMessage.getAttemptsCount() + 1));
      } else {
        advertMessage.setStatusCode(StatusCodeEnum.ATTEMPTS_EXPIRED);
      }
    }
    advertRepository.saveAll(advertMessages);
  }
}
