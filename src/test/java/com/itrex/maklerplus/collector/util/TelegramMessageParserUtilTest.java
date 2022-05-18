package com.itrex.maklerplus.collector.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.itrex.maklerplus.collector.entity.AdvertMessage;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TelegramMessageParserUtilTest {

  @Autowired private TelegramMessageParserUtil factory;

  @Test
  void createAdverts_validData_shouldCreateOneAdvert() {
    // given
    String messages =
        "      replyMarkup = null\n"
            + "    }\n"
            + "    Message {\n"
            + "      id = 40894464\n"
            + "      senderId = MessageSenderChat {\n"
            + "        chatId = -1001532834238\n"
            + "      }\n"
            + "      chatId = -1001532834238\n"
            + "      containsUnreadMention = false\n"
            + "      date = 1647239243\n"
            + "      editDate = 0\n"
            + "      content = MessageText {\n"
            + "        text = FormattedText {\n"
            + "          text = \"Аренда каартиры в тбилиси 800 $ 70 ка около метро სადგურის მოედანი #rent #apartment\"\n"
            + "          entities = vector[2] {";
    // when
    List<AdvertMessage> advertMessages = factory.createAdverts(messages);
    // then
    assertEquals(1, advertMessages.size());
    assertEquals("40894464", advertMessages.get(0).getNativeId());
    assertEquals("-1001532834238", advertMessages.get(0).getChatId());
    assertEquals(
        "Аренда каартиры в тбилиси 800 $ 70 ка около метро სადგურის მოედანი #rent #apartment",
        advertMessages.get(0).getText());
    assertEquals(1647239243000L, advertMessages.get(0).getDate());
  }

  @Test
  void createAdverts_invalidData_shouldReturnEmptyList() {
    // given
    String messages =
        "      replyMarkup = null\n"
            + "    }\n"
            + "    Message {\n"
            + "      id = 40894464\n"
            + "      senderId = MessageSenderChat {\n"
            + "        chatId = -1001532834238\n"
            + "      }\n"
            + "      chatId = -1001532834238\n"
            + "      containsUnreadMention = false\n"
            + "      date = 1647239243\n"
            + "      editDate = 0\n"
            + "      content = MessageText {\n"
            + "        text = FormattedText {\n"
            + "          text = \"too short\"\n"
            + "          entities = vector[2] {";
    // when
    List<AdvertMessage> advertMessages = factory.createAdverts(messages);
    // then
    assertTrue(advertMessages.isEmpty());
  }
}
