package ru.aif.aifback;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_CONFIRM_CREATE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import ru.aif.aifback.enums.BotSource;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.client.ClientRecordTime;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.response.AiRecordResponse;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.services.ai.recognize.VoiceRecognizeService;
import ru.aif.aifback.services.ai.record.RecordSearchService;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.client.ClientService;
import ru.aif.aifback.services.process.admin.AdminProcessService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType;
import ru.aif.aifback.services.user.UserBotService;
import ru.aif.aifback.services.user.UserCalendarService;
import ru.aif.aifback.services.user.UserItemService;
import ru.aif.aifback.services.utils.CommonUtils;

@SpringBootTest
class AifBackApplicationTests {

    @Autowired
    private UserCalendarService userCalendarService;
    @Autowired
    private UserItemService userItemService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientRecordService clientRecordService;
    @Autowired
    private AdminProcessService adminProcessService;
    @Autowired
    private VoiceRecognizeService voiceRecognizeService;
    @Autowired
    private RecordSearchService recordSearchService;
    @Autowired
    private UserBotService userBotService;

    @Test
    @Disabled
    @Sql("/sql/init.sql")
    void calcTimesTest() {
        Long year = 2025L;
        Long month = 11L;
        Long day = 24L;
        Long idUserBot = 28L;

        List<UserCalendar> userCalendars = userCalendarService.findAllDaysByMonthAndYearAndDay(year, month, day, idUserBot);
        Assertions.assertNotNull(userCalendars);

        Long userItemId = 31L;
        Optional<UserItem> userItem = userItemService.findUserItemById(userItemId);
        Assertions.assertNotNull(userItem);

        String tgId = "1487726317";
        Long clientId = clientService.getClientIdOrCreate(tgId);
        Assertions.assertNotNull(clientId);

        Optional<Long> clientRecordId = clientRecordService.addClientRecord(clientId,
                                                                            idUserBot,
                                                                            userItemId,
                                                                            108L,
                                                                            17L,
                                                                            12L,
                                                                            0L,
                                                                            null);
        Assertions.assertNotNull(clientRecordId);

        Map<String, List<ClientRecordTime>> times = new HashMap<>();
        for (UserCalendar userCalendar : userCalendars) {
            List<ClientRecord> records = clientRecordService.findAllRecordsByStaffAndDayAndStatus(
                    userCalendar.getAifUserStaffId(), userCalendar.getId(), idUserBot, ClientRecordType.ACTIVE.getType());

            List<ClientRecordTime> timesValue = CommonUtils.formatTimeCalendar(userCalendar,
                                                                               userItem.get(),
                                                                               userItemService.getMinUserItem(idUserBot),
                                                                               records);
            if (timesValue.isEmpty()) {
                continue;
            }

            timesValue.forEach(time -> {
                String key = String.format("%02d:%02d", time.getHours(), time.getMins());

                if (!times.containsKey(key)) {
                    times.put(key, new ArrayList<>());
                }
                times.get(key).add(time);
            });
        }

        Assertions.assertFalse(times.isEmpty());
        Assertions.assertEquals(times.size(), 6);
    }

    @Test
    @Disabled
    void testInputVoice() {
        String token = "7978891707:AAGUSUvMhbfgQJEglSP5lKfKC1yM17lYjUw";
        String fileId = "AwACAgIAAxkBAAISU2kts6Se_cMqq5oFhc9aD2r9RR2iAAJikwAC2TtwSXK8oHy_q05bNgQ";

        UserBot userBot = userBotService.findById(1L);
        Assertions.assertNotNull(userBot);

        String response = voiceRecognizeService.recognize(WebhookRequest.builder().token(token).fileId(fileId).build(), userBot);
        Assertions.assertNotNull(response);
    }

    @Test
    @Disabled
    void testAiRecord() throws Exception {
        String message = "Запиши меня на маникюр в следующий четверг на 10 утра";
        Long userBotId = 1L;
        String chatId = "1487726317";

        UserBot userBot = userBotService.findById(userBotId);
        Assertions.assertNotNull(userBot);

        AiRecordResponse response = recordSearchService.search(message, userBot, chatId);
        Assertions.assertNotNull(response);
    }

    @Disabled
    @Test
    void createUserBotTest() {
        String chatId = "1487726317";

        WebhookRequest tgWebhookRequest = WebhookRequest.builder()
                                                        .callback(TRUE)
                                                        .chatId(chatId)
                                                        .source(BotSource.TELEGRAM.getSource())
                                                        .text(String.format("%s;%s",
                                                                            BOT_CONFIRM_CREATE.getType(),
                                                                            1))
                                                        .build();
        adminProcessService.process(tgWebhookRequest);
    }

}
