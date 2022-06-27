package com.itrex.maklerplus.collector.controller;

import com.itrex.maklerplus.collector.exception.ServiceException;
import com.itrex.maklerplus.collector.scheduler.AdvertMessageResendScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduler")
@RequiredArgsConstructor
public class SchedulerController {

  private final AdvertMessageResendScheduler scheduler;

  @PostMapping("/resend")
  public ResponseEntity<String> resendScheduler() throws ServiceException {
    scheduler.resendAndUpdateAdvertMessages();
    return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
  }
}
