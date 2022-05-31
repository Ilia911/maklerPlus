package com.itrex.maklerplus.collector.util;

import com.itrex.maklerplus.collector.entity.AdvertMessage;
import com.itrex.maklerplus.collector.entity.HostAPIEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class TelegramMessageParserUtil {

  // todo: extract these values into configuration storage for Spring Cloud Config Server
  private static final String TEXT_PATTERN = "text = \"(.*?)\"";
  private static final String NATIVE_ID_PATTERN = "id = (\\d*)";
  private static final String CHAT_ID_PATTERN = "chatId = (.?\\d*)";
  private static final String NATIVE_CREATED_TIME_PATTERN = "date = (\\d*)";
  private static final String MESSAGES_SPLIT_PATTERN = "Message \\{";
  private static final int MINIMUM_LENGTH = 10;

  public List<AdvertMessage> createAdverts(String messages) {

    String[] messageArray = messages.split(MESSAGES_SPLIT_PATTERN);
    List<AdvertMessage> advertMessageList = new ArrayList<>();

    for (String message : messageArray) {

      String text = retrieveStringByPattern(message, TEXT_PATTERN);
      log.debug("Retrieved text: {}", text);

      if (text.length() > MINIMUM_LENGTH) {
        AdvertMessage advertMessage = new AdvertMessage();
        advertMessage.setText(text);
        advertMessage.setNativeId(retrieveStringByPattern(message, NATIVE_ID_PATTERN));
        advertMessage.setChatId(retrieveStringByPattern(message, CHAT_ID_PATTERN));
        advertMessage.setNativeCreatedTime(retrieveDateInMilliseconds(message));
        advertMessage.setHostAPIEnum(HostAPIEnum.TELEGRAM);
        advertMessageList.add(advertMessage);
      }
    }
    return advertMessageList;
  }

  private Long retrieveDateInMilliseconds(String message) {

    String dateInSeconds = retrieveStringByPattern(message, NATIVE_CREATED_TIME_PATTERN);

    return Long.parseLong(dateInSeconds) * 1000;
  }

  private String retrieveStringByPattern(String message, String stringPattern) {

    Pattern pattern = Pattern.compile(stringPattern, Pattern.DOTALL);
    Matcher matcher = pattern.matcher(message);

    String result = "";

    if (matcher.find()) {
      result = matcher.group(1);
    }
    return result;
  }
}
