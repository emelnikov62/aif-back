package ru.aif.aifback.services.process.tg.client.bot.record;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.NULL_PARAM;
import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.enums.BotType.BOT_RECORD;
import static ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType.BOT_AI_RECORD_PROCESS;
import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.MENU_TITLE;
import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.createMainMenuKeyboard;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.enums.BotType;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.BotProcessService;
import ru.aif.aifback.services.process.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.utils.CommonUtils;

/**
 * TG Client API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgRecordBotService implements BotProcessService {

    private final List<TgClientBotOperationService> operations;

    /**
     * Webhook process.
     * @param webhookRequest webhookAdminRequest
     * @param userBot user bot
     * @return true/false
     */
    @Override
    public Boolean process(WebhookRequest webhookRequest, UserBot userBot) {
        if (webhookRequest.isCallback()) {
            processCallback(webhookRequest, userBot);
        } else {
            processNoCallback(webhookRequest, userBot);
        }

        return TRUE;
    }

    /**
     * Callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void processCallback(WebhookRequest webhookRequest, UserBot userBot) {
        TelegramBot bot = new TelegramBot(userBot.getToken());
        try {
            TgClientBotOperationService operation = operations.stream()
                                                              .filter(f -> webhookRequest.getText().contains(f.getOperationType().getType()))
                                                              .findFirst()
                                                              .orElse(null);
            if (Objects.isNull(operation)) {
                CommonUtils.sendMessage(webhookRequest.getChatId(),
                                        Integer.parseInt(webhookRequest.getMessageId()),
                                        MENU_TITLE,
                                        createMainMenuKeyboard(userBot.getBot().getType()),
                                        bot,
                                        TRUE);
                return;
            }

            operation.process(webhookRequest, userBot, bot);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            CommonUtils.sendMessage(TG_LOG_ID, Integer.parseInt(webhookRequest.getMessageId()), e.getMessage(), bot, FALSE);
        }
    }

    /**
     * No callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void processNoCallback(WebhookRequest webhookRequest, UserBot userBot) {
        if (Objects.nonNull(webhookRequest.getFileId()) && !Objects.equals(webhookRequest.getFileId(), NULL_PARAM)) {
            TgClientBotOperationService aiOperation = operations.stream()
                                                                .filter(f -> Objects.equals(f.getOperationType(), BOT_AI_RECORD_PROCESS))
                                                                .findFirst()
                                                                .orElse(null);
            if (Objects.nonNull(aiOperation)) {
                aiOperation.process(webhookRequest, userBot, new TelegramBot(userBot.getToken()));
                return;
            }
        }

        CommonUtils.sendMessage(webhookRequest.getChatId(),
                                Integer.parseInt(webhookRequest.getMessageId()),
                                MENU_TITLE,
                                createMainMenuKeyboard(userBot.getBot().getType()),
                                new TelegramBot(userBot.getToken()),
                                TRUE);
    }

    /**
     * Get bot type.
     * @return bot type
     */
    @Override
    public BotType getBotType() {
        return BOT_RECORD;
    }

}
