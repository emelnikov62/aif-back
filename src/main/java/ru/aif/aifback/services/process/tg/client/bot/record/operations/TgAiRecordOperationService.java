package ru.aif.aifback.services.process.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.utils.CommonUtils.sendMessage;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType;
import ru.aif.aifback.services.utils.CommonUtils;

/**
 * TG AI record operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgAiRecordOperationService implements TgClientBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(WebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addRow(createBackButton(ClientRecordBotOperationType.BOT_MAIN.getType()));

        String answer = "❗Просто запишите голосовое и отправьте сюда ❗";
        CommonUtils.sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientRecordBotOperationType getOperationType() {
        return ClientRecordBotOperationType.BOT_AI_RECORD;
    }
}
