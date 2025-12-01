package ru.aif.aifback;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.MESSAGE_ID_EMPTY;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_DAY;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_MONTH;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_SHOW_BY_DAY;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_YEAR;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.ACTIVE;

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

import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.client.ClientRecordTime;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.client.ClientService;
import ru.aif.aifback.services.tg.admin.TgAdminService;
import ru.aif.aifback.services.tg.enums.TgClientRecordType;
import ru.aif.aifback.services.tg.utils.TgUtils;
import ru.aif.aifback.services.user.UserCalendarService;
import ru.aif.aifback.services.user.UserItemService;

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
    private TgAdminService tgAdminService;

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
                    userCalendar.getAifUserStaffId(), userCalendar.getId(), idUserBot, TgClientRecordType.ACTIVE.getType());

            List<ClientRecordTime> timesValue = TgUtils.formatTimeCalendar(userCalendar,
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

    @Disabled
    @Test
    void routeAdminRecordsMenuTest() {
        String id = "28";
        String chatId = "1487726317";
        String messageId = String.valueOf(MESSAGE_ID_EMPTY);

        TgWebhookRequest tgWebhookRequest = TgWebhookRequest.builder()
                                                            .id(id)
                                                            .callback(TRUE)
                                                            .chatId(chatId)
                                                            .messageId(messageId)
                                                            .text(String.format("%s;%s", BOT_RECORDS.getType(), id))
                                                            .build();
        tgAdminService.process(tgWebhookRequest);
    }

    @Disabled
    @Test
    void routeAdminRecordYearsMenuTest() {
        String id = "28";
        String chatId = "1487726317";
        String messageId = String.valueOf(MESSAGE_ID_EMPTY);

        TgWebhookRequest tgWebhookRequest = TgWebhookRequest.builder()
                                                            .id(id)
                                                            .callback(TRUE)
                                                            .chatId(chatId)
                                                            .messageId(messageId)
                                                            .text(String.format("%s;%s;%s", BOT_RECORD_YEAR.getType(), ACTIVE.getType(), id))
                                                            .build();
        tgAdminService.process(tgWebhookRequest);
    }

    @Disabled
    @Test
    void routeAdminRecordMonthsMenuTest() {
        String id = "28";
        String chatId = "1487726317";
        String messageId = String.valueOf(MESSAGE_ID_EMPTY);

        TgWebhookRequest tgWebhookRequest = TgWebhookRequest.builder()
                                                            .id(id)
                                                            .callback(TRUE)
                                                            .chatId(chatId)
                                                            .messageId(messageId)
                                                            .text(String.format("%s;%s;%s;%s", BOT_RECORD_MONTH.getType(), 2025, id,
                                                                                ACTIVE.getType()))
                                                            .build();
        tgAdminService.process(tgWebhookRequest);
    }

    @Disabled
    @Test
    void routeAdminRecordDaysMenuTest() {
        String id = "28";
        String chatId = "1487726317";
        String messageId = String.valueOf(MESSAGE_ID_EMPTY);

        TgWebhookRequest tgWebhookRequest = TgWebhookRequest.builder()
                                                            .id(id)
                                                            .callback(TRUE)
                                                            .chatId(chatId)
                                                            .messageId(messageId)
                                                            .text(String.format("%s;%s;%s;%s;%s",
                                                                                BOT_RECORD_DAY.getType(),
                                                                                12,
                                                                                2025,
                                                                                id,
                                                                                ACTIVE.getType()))
                                                            .build();
        tgAdminService.process(tgWebhookRequest);
    }

    @Disabled
    @Test
    void routeAdminRecordShowByDayMenuTest() {
        String id = "28";
        String chatId = "1487726317";
        String messageId = String.valueOf(MESSAGE_ID_EMPTY);

        TgWebhookRequest tgWebhookRequest = TgWebhookRequest.builder()
                                                            .id(id)
                                                            .callback(TRUE)
                                                            .chatId(chatId)
                                                            .messageId(messageId)
                                                            .text(String.format("%s;%s;%s;%s;%s;%s",
                                                                                BOT_RECORD_SHOW_BY_DAY.getType(),
                                                                                12,
                                                                                12,
                                                                                2025,
                                                                                id,
                                                                                ACTIVE.getType()))
                                                            .build();
        tgAdminService.process(tgWebhookRequest);
    }

}
