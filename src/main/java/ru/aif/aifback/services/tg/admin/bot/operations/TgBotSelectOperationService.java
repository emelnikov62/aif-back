package ru.aif.aifback.services.tg.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_CALENDAR_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_ITEMS_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_STAFF_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_STATS_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.DELETE_BOT_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.LINK_TOKEN_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.MENU_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createBackButton;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.getBotIconByType;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_DELETE;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_SELECT;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_STATS;
import static ru.aif.aifback.services.tg.enums.TgBotType.BOT_RECORD;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.WebAppInfo;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.tg.admin.TgAdminBotOperationService;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;
import ru.aif.aifback.services.user.UserBotService;

/**
 * TG Admin Bot select operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotSelectOperationService implements TgAdminBotOperationService {

    private final UserBotService userBotService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        String answer = processBotSelect(webhookRequest.getText().split(DELIMITER)[1], keyboard);
        keyboard.addRow(createBackButton(BOT_MAIN.getType()));

        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
    }

    /**
     * Process bot select button.
     * @param userBotId user bot id
     * @param keyboard keyboard
     */
    private String processBotSelect(String userBotId, InlineKeyboardMarkup keyboard) {
        Optional<UserBot> userBot = userBotService.getUserBot(Long.valueOf(userBotId));
        if (userBot.isEmpty()) {
            return MENU_TITLE;
        }

        if (Objects.isNull(userBot.get().getToken())) {
            keyboard.addRow(new InlineKeyboardButton(LINK_TOKEN_TITLE).webApp(
                    new WebAppInfo("https://aif-back-emelnikov62.amvera.io/aif/admin/link-bot-form?id=" + userBotId)));
        } else {
            keyboard.addRow(new InlineKeyboardButton(BOT_STATS_TITLE).callbackData(String.format("%s;%s", BOT_STATS.getType(), userBotId)),
                            new InlineKeyboardButton(BOT_ITEMS_TITLE).webApp(
                                    new WebAppInfo("https://aif-back-emelnikov62.amvera.io/aif/admin/items-bot-form?id=" + userBotId)));

            if (Objects.equals(userBot.get().getBot().getType(), BOT_RECORD.getType())) {
                keyboard.addRow(new InlineKeyboardButton(BOT_STAFF_TITLE).webApp(
                                        new WebAppInfo("https://aif-back-emelnikov62.amvera.io/aif/admin/staff-bot-form?id=" + userBotId)),
                                new InlineKeyboardButton(BOT_CALENDAR_TITLE).webApp(
                                        new WebAppInfo("https://aif-back-emelnikov62.amvera.io/aif/admin/calendar-bot-form?id=" + userBotId)));
            }
        }

        keyboard.addRow(new InlineKeyboardButton(DELETE_BOT_TITLE).callbackData(String.format("%s;%s", BOT_DELETE.getType(), userBotId)));
        return String.format("%s %s (ID: %s)",
                             getBotIconByType(userBot.get().getBot().getType()),
                             userBot.get().getBot().getDescription(),
                             userBot.get().getId());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return BOT_SELECT;
    }
}
