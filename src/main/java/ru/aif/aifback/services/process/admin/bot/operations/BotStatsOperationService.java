package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.COLUMNS_STATS;
import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_STATS_TITLE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_SELECT;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_STATS;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_STATS_SELECT;
import static ru.aif.aifback.services.process.admin.enums.AdminStatsType.ALL;
import static ru.aif.aifback.services.process.admin.enums.AdminStatsType.MONTH;
import static ru.aif.aifback.services.process.admin.enums.AdminStatsType.YEAR;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.services.process.admin.AdminBotOperationService;
import ru.aif.aifback.services.process.admin.enums.AdminBotOperationType;
import ru.aif.aifback.services.process.admin.enums.AdminStatsType;

/**
 * Admin Bot stats operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotStatsOperationService implements AdminBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        String userBotId = webhookRequest.getText().split(DELIMITER)[1];
        List<ChatMessage.Button> buttons = new ArrayList<>();

        Function<AdminStatsType, String> statsName = (type) -> String.format("%s %s", type.getIcon(), type.getName());
        BiFunction<AdminStatsType, String, String> callbackData = (type, id) -> String.format("%s;%s;%s", BOT_STATS_SELECT.getType(), type.getType(),
                                                                                              id);

        buttons.add(ChatMessage.Button.builder()
                                      .title(statsName.apply(MONTH))
                                      .callback(callbackData.apply(MONTH, userBotId))
                                      .isBack(FALSE)
                                      .build());
        buttons.add(ChatMessage.Button.builder()
                                      .title(statsName.apply(YEAR))
                                      .callback(callbackData.apply(YEAR, userBotId))
                                      .isBack(FALSE)
                                      .build());
        buttons.add(ChatMessage.Button.builder()
                                      .title(statsName.apply(ALL))
                                      .callback(callbackData.apply(ALL, userBotId))
                                      .isBack(FALSE)
                                      .build());
        buttons.addAll(createBackButton(String.format("%s;%s", BOT_SELECT.getType(), userBotId)));

        return List.of(ChatMessage.builder()
                                  .text(BOT_STATS_TITLE)
                                  .updated(TRUE)
                                  .source(findByType(webhookRequest.getSource()))
                                  .chatId(webhookRequest.getChatId())
                                  .messageId(webhookRequest.getMessageId())
                                  .columns(COLUMNS_STATS)
                                  .buttons(buttons)
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public AdminBotOperationType getOperationType() {
        return BOT_STATS;
    }
}
