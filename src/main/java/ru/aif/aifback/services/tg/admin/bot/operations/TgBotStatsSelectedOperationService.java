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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        BigDecimal amount = BigDecimal.valueOf(records.stream()
                                                      .filter(f -> Objects.equals(f.getStatus(), FINISHED.getType()))
                                                      .map(ClientRecord::getUserItem)
                                                      .mapToDouble(f -> f.getAmount().doubleValue())
                                                      .sum());
        String answer = String.format("%s: %s \n\n", BOT_STATS_TITLE, type.getName()) +
                        String.format("<b>Прибыль:</b> %s руб.\n\n", amount) +
                        String.format("<b>Услуг:</b> %s%s %s%s %s%s\n\n",
                                      ACTIVE.getIcon(), calcCountByType(records, ACTIVE),
                                      CANCEL.getIcon(), calcCountByType(records, CANCEL),
                                      FINISHED.getIcon(), calcCountByType(records, FINISHED)) +
                        "<b>Специалисты:</b>\n" +
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
            staffs.append(String.format("%s<b>%s:</b>\n", SPACE, staff.getKey()))
                  .append(SPACE)
                  .append(String.format("%s%s %s%s %s%s",
                                        ACTIVE.getIcon(), calcCountByType(staff.getValue(), ACTIVE),
                                        CANCEL.getIcon(), calcCountByType(staff.getValue(), CANCEL),
                                        FINISHED.getIcon(), calcCountByType(staff.getValue(), FINISHED)));
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
