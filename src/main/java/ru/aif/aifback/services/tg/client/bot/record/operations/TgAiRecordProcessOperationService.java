package ru.aif.aifback.services.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.AI_RECORD_ERROR_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.AI_RECORD_SUCCESS_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_AI_RECORD_PROCESS;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.ai.recognize.VoiceRecognizeService;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;

/**
 * TG AI record operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgAiRecordProcessOperationService implements TgClientBotOperationService {

    private final VoiceRecognizeService voiceRecognizeService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addRow(createBackButton(BOT_MAIN.getType()));

        String answer = voiceRecognizeService.recognize(webhookRequest, AI_RECORD_SUCCESS_TITLE, AI_RECORD_ERROR_TITLE, userBot);
        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return BOT_AI_RECORD_PROCESS;
    }
}
