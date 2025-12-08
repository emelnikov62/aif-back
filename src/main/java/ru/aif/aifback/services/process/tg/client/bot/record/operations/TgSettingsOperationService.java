package ru.aif.aifback.services.process.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.SETTINGS_TITLE;
import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType.BOT_SETTINGS;
import static ru.aif.aifback.services.utils.CommonUtils.sendMessage;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.utils.CommonUtils;
import ru.aif.aifback.services.process.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType;

/**
 * TG Settings operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgSettingsOperationService implements TgClientBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(WebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addRow(createBackButton(BOT_MAIN.getType()));

        CommonUtils.sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), SETTINGS_TITLE, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientRecordBotOperationType getOperationType() {
        return BOT_SETTINGS;
    }
}
