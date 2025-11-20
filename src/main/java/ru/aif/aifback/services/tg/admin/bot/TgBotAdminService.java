package ru.aif.aifback.services.tg.admin.bot;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.constants.Constants.TG_TOKEN_ADMIN;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BACK_TO_BUY_BOTS_MENU;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BACK_TO_MAIN_MENU;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BACK_TO_MY_BOTS_MENU;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOTS_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_CREATE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_DELETE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_SELECT;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_STATS;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BUY_BOT;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.CREATE_BOT_ERROR_ANSWER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.CREATE_BOT_SUCCESS_ANSWER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.MENU_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.MY_BOTS;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.MY_BOTS_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.SELECT_BOT_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createMainMenuKeyboard;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.dictionary.Bot;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.tg.TgBotService;
import ru.aif.aifback.services.tg.enums.TgClientTypeBot;
import ru.aif.aifback.services.tg.utils.TgUtils;
import ru.aif.aifback.services.user.BotService;
import ru.aif.aifback.services.user.UserBotService;

/**
 * TG Admin API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotAdminService implements TgBotService {

    private final UserBotService userBotService;
    private final BotService botService;
    private TelegramBot bot;

    /**
     * Post construct.
     */
    @PostConstruct
    void init() {
        bot = new TelegramBot(TG_TOKEN_ADMIN);
    }

    /**
     * Webhook process.
     * @param webhookRequest webhookAdminRequest
     * @param userBot user bot
     * @return true/false
     */
    @Override
    public Boolean process(TgWebhookRequest webhookRequest, UserBot userBot) {
        if (webhookRequest.isCallback()) {
            processCallback(webhookRequest, userBot);
        } else {
            processNoCallback(webhookRequest, userBot);
        }

        return Boolean.TRUE;
    }

    /**
     * Callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void processCallback(TgWebhookRequest webhookRequest, UserBot userBot) {
        try {
            String answer = null;
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

            if (Objects.equals(webhookRequest.getText(), BACK_TO_MAIN_MENU)) {
                answer = MENU_TITLE;
                keyboard = createMainMenuKeyboard();
            }

            if (webhookRequest.getText().contains(BOT_SELECT)) {
                answer = processBotSelect(webhookRequest.getText(), keyboard);
            }

            if (Objects.equals(webhookRequest.getText(), BUY_BOT) || Objects.equals(webhookRequest.getText(), BACK_TO_BUY_BOTS_MENU)) {
                answer = SELECT_BOT_TITLE;
                processBuyBot(keyboard);
                keyboard.addRow(TgAdminBotButtons.createBackButton(BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), MY_BOTS)
                || Objects.equals(webhookRequest.getText(), BACK_TO_MY_BOTS_MENU)
                || webhookRequest.getText().contains(BOT_DELETE)
                || webhookRequest.getText().contains(BOT_CREATE)) {
                answer = processUserBot(webhookRequest.getChatId(), webhookRequest.getText(), keyboard);
                keyboard.addRow(TgAdminBotButtons.createBackButton(BACK_TO_MAIN_MENU));
            }

            if (webhookRequest.getText().contains(BOT_STATS)) {
                answer = MENU_TITLE;
                keyboard.addRow(TgAdminBotButtons.createBackButton(BACK_TO_MAIN_MENU));
            }

            if (Objects.isNull(answer)) {
                TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), MENU_TITLE, bot);
            } else {
                TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), answer, keyboard, bot);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TgUtils.sendMessage(TG_LOG_ID, e.getMessage(), bot);
        }
    }

    /**
     * Process user bots.
     * @param id id
     * @param text text
     * @param keyboard keyboard
     */
    private String processUserBot(String id, String text, InlineKeyboardMarkup keyboard) {
        String answer = null;

        if (text.contains(BOT_DELETE)) {
            Long userBotId = Long.valueOf(text.split(DELIMITER)[1]);
            answer = userBotService.deleteUserBot(userBotId);
        }

        if (text.contains(BOT_CREATE)) {
            Long botId = Long.valueOf(text.split(DELIMITER)[1]);
            if (userBotService.createUserBot(id, botId)) {
                answer = CREATE_BOT_SUCCESS_ANSWER;
            } else {
                answer = CREATE_BOT_ERROR_ANSWER;
            }
        }

        List<UserBot> userBots = userBotService.getUserBotsByTgId(id);
        if (!userBots.isEmpty()) {
            userBots.forEach(userBot -> {
                keyboard.addRow(new InlineKeyboardButton(String.format("%s %s (ID: %s) %s",
                                                                       getBotIconByType(userBot.getBot().getType()),
                                                                       userBot.getBot().getDescription(),
                                                                       userBot.getId(),
                                                                       (userBot.isActive() && Objects.nonNull(userBot.getToken()) ? "✅" : "❌")))
                                        .callbackData(String.format("%s;%s", BOT_SELECT, userBot.getId())));
            });

            answer = Objects.isNull(answer) ? MY_BOTS_TITLE : answer;
        } else {
            answer = Objects.isNull(answer) ? BOTS_EMPTY_TITLE : answer;
        }

        return answer;
    }

    /**
     * No callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void processNoCallback(TgWebhookRequest webhookRequest, UserBot userBot) {
        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), MENU_TITLE, TgAdminBotButtons.createMainMenuKeyboard(), bot);
    }

    /**
     * Get bot type.
     * @return bot type
     */
    @Override
    public TgClientTypeBot getBotType() {
        return TgClientTypeBot.BOT_ADMIN;
    }

    /**
     * Process buy bot.
     * @param keyboard keyboard
     */
    private void processBuyBot(InlineKeyboardMarkup keyboard) {
        List<Bot> bots = botService.getBots();
        if (bots.isEmpty()) {
            return;
        }

        bots.forEach(bot -> keyboard.addRow(new InlineKeyboardButton(String.format("%s %s", getBotIconByType(bot.getType()), bot.getDescription()))
                                                    .callbackData(String.format("%s;%s", BOT_CREATE, bot.getId()))));
    }

    /**
     * Process bot select button.
     * @param text text
     * @param keyboard keyboard
     */
    private String processBotSelect(String text, InlineKeyboardMarkup keyboard) {
        String userBotId = text.split(DELIMITER)[1];

        Optional<UserBot> userBot = userBotService.getUserBot(Long.valueOf(userBotId));
        if (userBot.isEmpty()) {
            return MENU_TITLE;
        }

        if (Objects.isNull(userBot.get().getToken())) {
            keyboard.addRow(TgAdminBotButtons.createLinkBotButton(userBotId));
        } else {
            keyboard.addRow(TgAdminBotButtons.createSelectedBotMenu(userBotId));

            if (Objects.equals(userBot.get().getBot().getType(), TgClientTypeBot.BOT_RECORD.getType())) {
                keyboard.addRow(TgAdminBotButtons.createCalendarStaffBotButtons(userBotId));
            }
        }

        keyboard.addRow(TgAdminBotButtons.createDeleteBotButton(userBotId), TgAdminBotButtons.createBackButton(BACK_TO_MY_BOTS_MENU));
        return String.format("%s %s (ID: %s)",
                             getBotIconByType(userBot.get().getBot().getType()),
                             userBot.get().getBot().getDescription(),
                             userBot.get().getId());
    }

    /**
     * Get bot icon.
     * @param type type
     * @return bot icon
     */
    private String getBotIconByType(String type) {
        String icon = "\uD83C\uDF10";

        if (Objects.equals(type, TgClientTypeBot.BOT_RECORD.getType())) {
            icon = "\uD83D\uDCDD";
        }

        return icon;
    }
}
