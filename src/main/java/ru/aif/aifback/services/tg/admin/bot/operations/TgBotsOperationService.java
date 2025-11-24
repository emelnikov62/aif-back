package ru.aif.aifback.services.tg.admin.bot.operations;

import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOTS_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.MY_BOTS_TITLE;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.tg.admin.TgAdminBotOperationService;
import ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;
import ru.aif.aifback.services.tg.utils.TgUtils;
import ru.aif.aifback.services.user.UserBotService;

/**
 * TG Admin Bots operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotsOperationService implements TgAdminBotOperationService {

    private final UserBotService userBotService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        List<UserBot> userBots = userBotService.getUserBotsByTgId(webhookRequest.getChatId());
        userBots.forEach(userBot -> {
            keyboard.addRow(new InlineKeyboardButton(String.format("%s %s (ID: %s) %s",
                                                                   TgAdminBotButtons.getBotIconByType(userBot.getBot().getType()),
                                                                   userBot.getBot().getDescription(),
                                                                   userBot.getId(),
                                                                   (userBot.isActive() && Objects.nonNull(userBot.getToken()) ? "✅" : "❌")))
                                    .callbackData(String.format("%s;%s", TgAdminBotOperationType.BOT_SELECT.getType(), userBot.getId())));
        });

        keyboard.addRow(TgAdminBotButtons.createBackButton(TgAdminBotOperationType.BOT_MAIN.getType()));
        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), userBots.isEmpty() ? BOTS_EMPTY_TITLE : MY_BOTS_TITLE, keyboard, bot);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return TgAdminBotOperationType.BOTS_BOTS;
    }
}
