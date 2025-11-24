package ru.aif.aifback.services.tg.admin.bot.operations;

import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOTS_TO_CREATE_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.SELECT_BOT_TITLE;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.dictionary.Bot;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.tg.admin.TgAdminBotOperationService;
import ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;
import ru.aif.aifback.services.tg.utils.TgUtils;
import ru.aif.aifback.services.user.BotService;

/**
 * TG Admin Bot create operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotCreateOperationService implements TgAdminBotOperationService {

    private final BotService botService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String answer = SELECT_BOT_TITLE;
        List<Bot> bots = botService.getBots();
        if (bots.isEmpty()) {
            answer = BOTS_TO_CREATE_EMPTY_TITLE;
        } else {
            bots.forEach(b -> keyboard.addRow(new InlineKeyboardButton(String.format("%s %s",
                                                                                     TgAdminBotButtons.getBotIconByType(b.getType()),
                                                                                     b.getDescription()))
                                                      .callbackData(String.format("%s;%s",
                                                                                  TgAdminBotOperationType.BOT_CONFIRM_CREATE.getType(),
                                                                                  b.getId()))));
        }

        keyboard.addRow(TgAdminBotButtons.createBackButton(TgAdminBotOperationType.BOT_MAIN.getType()));

        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), answer, keyboard, bot);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return TgAdminBotOperationType.BOT_CREATE;
    }
}
