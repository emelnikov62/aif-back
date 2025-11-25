package ru.aif.aifback;

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
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.client.ClientService;
import ru.aif.aifback.services.tg.enums.TgClientRecordType;
import ru.aif.aifback.services.tg.utils.TgUtils;
import ru.aif.aifback.services.user.UserCalendarService;
import ru.aif.aifback.services.user.UserItemService;

@SpringBootTest
@Sql("/sql/init.sql")
class AifBackApplicationTests {

    @Autowired
    private UserCalendarService userCalendarService;
    @Autowired
    private UserItemService userItemService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientRecordService clientRecordService;

    @Test
    @Disabled
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

}
