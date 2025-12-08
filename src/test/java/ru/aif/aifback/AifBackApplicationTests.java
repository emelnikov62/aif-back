package ru.aif.aifback;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.AI_SEARCH_URL;
import static ru.aif.aifback.constants.Constants.MESSAGE_ID_EMPTY;
import static ru.aif.aifback.constants.Constants.YANDEX_API_KEY;
import static ru.aif.aifback.constants.Constants.YANDEX_API_RECOGNIZE_URL;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_CONFIRM_CREATE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_DAY;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_MONTH;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_SHOW_BY_DAY;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_YEAR;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_SELECT;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.ACTIVE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import ru.aif.aifback.enums.BotSource;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.client.ClientRecordTime;
import ru.aif.aifback.model.requests.AiRecordRequest;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.response.AiRecordResponse;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.client.ClientService;
import ru.aif.aifback.services.process.admin.AdminProcessService;
import ru.aif.aifback.services.process.client.enums.ClientRecordType;
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
    private AdminProcessService tgAdminService;

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

    @Disabled
    @Test
    void routeAdminRecordsMenuTest() {
        String id = "28";
        String chatId = "1487726317";
        String messageId = String.valueOf(MESSAGE_ID_EMPTY);

        WebhookRequest tgWebhookRequest = WebhookRequest.builder()
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

        WebhookRequest tgWebhookRequest = WebhookRequest.builder()
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

        WebhookRequest tgWebhookRequest = WebhookRequest.builder()
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

        WebhookRequest tgWebhookRequest = WebhookRequest.builder()
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

        WebhookRequest tgWebhookRequest = WebhookRequest.builder()
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

    @Disabled
    @Test
    void testInputVoice() {
        String token = "7978891707:AAGUSUvMhbfgQJEglSP5lKfKC1yM17lYjUw";
        TelegramBot bot = new TelegramBot(token);
        GetFile request = new GetFile("AwACAgIAAxkBAAISU2kts6Se_cMqq5oFhc9aD2r9RR2iAAJikwAC2TtwSXK8oHy_q05bNgQ");
        GetFileResponse getFileResponse = bot.execute(request);

        File file = getFileResponse.file();
        file.fileId();
        file.filePath();
        file.fileSize();

        String fullPath = bot.getFullFilePath(file);
        Assertions.assertNotNull(fullPath);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = new URL(fullPath).openStream();

            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }

            Assertions.assertNotNull(outputStream.toByteArray());

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("Authorization", YANDEX_API_KEY);

            HttpEntity<byte[]> entity = new HttpEntity<>(outputStream.toByteArray(), headers);
            ResponseEntity<String> response = restTemplate.exchange(YANDEX_API_RECOGNIZE_URL, HttpMethod.POST, entity, String.class);

            Assertions.assertNotNull(response.getBody());
        } catch (IOException e) {
            Assertions.assertEquals(1, 0);
        }
    }

    @Disabled
    @Test
    void testAiRecord() throws Exception {
        String message = "Запиши меня на маникюр в следующий четверг на 10 утра";
        Long userBotId = 28L;
        String tgId = "1487726317";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json = new ObjectMapper().writeValueAsString(AiRecordRequest.builder()
                                                                           .chatId(tgId)
                                                                           .prompt(message)
                                                                           .userBotId(userBotId)
                                                                           .build());
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        ResponseEntity<AiRecordResponse> response = restTemplate.exchange(AI_SEARCH_URL, HttpMethod.POST, entity, AiRecordResponse.class);
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
        tgAdminService.process(tgWebhookRequest);
    }

    @Test
    void selectUserBotTest() {
        String chatId = "1487726317";

        WebhookRequest tgWebhookRequest = WebhookRequest.builder()
                                                        .callback(TRUE)
                                                        .chatId(chatId)
                                                        .source(BotSource.TELEGRAM.getSource())
                                                        .text(String.format("%s;%s",
                                                                            BOT_SELECT.getType(),
                                                                            1))
                                                        .build();
        tgAdminService.process(tgWebhookRequest);
    }

}
