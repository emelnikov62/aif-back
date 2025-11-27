package ru.aif.aifback.services.tg.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_STATS_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_STATS;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_STATS_SELECT;
import static ru.aif.aifback.services.tg.enums.TgAdminStatsType.findByType;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.ACTIVE;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.CANCEL;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.FINISHED;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserStaff;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.tg.admin.TgAdminBotOperationService;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;
import ru.aif.aifback.services.tg.enums.TgAdminStatsType;
import ru.aif.aifback.services.tg.enums.TgClientRecordType;

/**
 * TG Admin Bot stats selected operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotStatsSelectedOperationService implements TgAdminBotOperationService {

    private static final String SPACE = "    ";
    private static final BiFunction<TgClientRecordType, Integer, String> FORMAT_SERVICE =
            (type, count) -> String.format("%s%s %s: <b>%s</b>\n\n", SPACE, type.getIcon(), type.getNameStats(), count);
    private final ClientRecordService clientRecordService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String[] params = webhookRequest.getText().split(DELIMITER);
        TgAdminStatsType type = findByType(params[1]);
        String userBotId = params[2];

        List<ClientRecord> records = clientRecordService.findByPeriod(type, Long.valueOf(userBotId));

        String answer = String.format("%s: %s \n\n", BOT_STATS_TITLE, type.getName()) +
                        "<b>Услуг:</b>\n\n" +
                        FORMAT_SERVICE.apply(ACTIVE, calcCountByType(records, ACTIVE)) +
                        FORMAT_SERVICE.apply(CANCEL, calcCountByType(records, CANCEL)) +
                        FORMAT_SERVICE.apply(FINISHED, calcCountByType(records, FINISHED)) +
                        "\n<b>Специалисты:</b>\n\n" +
                        fillRecordStaffs(records);

        keyboard.addRow(createBackButton(String.format("%s;%s", BOT_STATS.getType(), userBotId)));
        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
    }

    /**
     * Fill client records staffs.
     * @param records records
     * @return staffs
     */
    private String fillRecordStaffs(List<ClientRecord> records) {
        StringBuilder staffs = new StringBuilder();
        Map<String, List<ClientRecord>> staffMap = new HashMap<>();
        records.forEach(record -> {
            String key = getStaffKey(record.getUserStaff());

            if (!staffMap.containsKey(key)) {
                staffMap.put(key, new ArrayList<>());
            }

            staffMap.get(key).add(record);
        });

        for (Map.Entry<String, List<ClientRecord>> staff : staffMap.entrySet()) {
            staffs.append(String.format("%s%s:\n", SPACE, staff.getKey()))
                  .append(SPACE).append(SPACE).append(FORMAT_SERVICE.apply(ACTIVE, calcCountByType(staff.getValue(), ACTIVE)))
                  .append(SPACE).append(SPACE).append(FORMAT_SERVICE.apply(CANCEL, calcCountByType(staff.getValue(), CANCEL)))
                  .append(SPACE).append(SPACE).append(FORMAT_SERVICE.apply(FINISHED, calcCountByType(staff.getValue(), FINISHED)));
        }

        return staffs.toString();
    }

    /**
     * Get user staff key.
     * @param userStaff user staff
     * @return key
     */
    private String getStaffKey(UserStaff userStaff) {
        return String.format("%s %s %s", userStaff.getSurname(), userStaff.getName(), userStaff.getThird());
    }

    /**
     * Calculate records type count.
     * @param records records
     * @param type type
     * @return count
     */
    private int calcCountByType(List<ClientRecord> records, TgClientRecordType type) {
        return records.stream().filter(r -> Objects.equals(r.getStatus(), type.getType())).toList().size();
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return BOT_STATS_SELECT;
    }
}
