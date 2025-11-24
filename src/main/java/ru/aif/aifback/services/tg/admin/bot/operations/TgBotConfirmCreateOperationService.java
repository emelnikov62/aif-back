package ru.aif.aifback.services.tg.admin.bot.operations;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.CREATE_BOT_ERROR_ANSWER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.CREATE_BOT_SUCCESS_ANSWER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.MY_BOTS_TITLE;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.tg.admin.TgAdminBotOperationService;
import ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;
import ru.aif.aifback.services.tg.utils.TgUtils;
import ru.aif.aifback.services.user.UserBotService;

/**
 * TG Admin Bot confirm create operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotConfirmCreateOperationService implements TgAdminBotOperationService {

    private final UserBotService userBotService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        Long botId = Long.valueOf(webhookRequest.getText().split(DELIMITER)[1]);
        String answer;
        if (userBotService.createUserBot(webhookRequest.getId(), botId)) {
            answer = CREATE_BOT_SUCCESS_ANSWER;
            keyboard.addRow(new InlineKeyboardButton(MY_BOTS_TITLE).callbackData(TgAdminBotOperationType.BOTS_BOTS.getType()));
        } else {
            answer = CREATE_BOT_ERROR_ANSWER;
            keyboard.addRow(TgAdminBotButtons.createBackButton(TgAdminBotOperationType.BOT_MAIN.getType()));
        }

        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), answer, keyboard, bot);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return TgAdminBotOperationType.BOT_CONFIRM_CREATE;
    }
}
