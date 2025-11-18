package ru.aif.aifback.services.tg.admin;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.constants.Constants.TG_TOKEN_ADMIN;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.BACK_TO_BUY_BOTS_MENU;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.BACK_TO_MAIN_MENU;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.BACK_TO_MY_BOTS_MENU;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.BOTS_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.BOT_CREATE;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.BOT_DELETE;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.BOT_SELECT;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.BUY_BOT;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.MENU_TITLE;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.MY_BOTS;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.SELECT_BOT_TITLE;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.createMainMenuKeyboard;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.Bot;
import ru.aif.aifback.model.UserBot;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.tg.TgService;
import ru.aif.aifback.services.tg.TgUtils;
import ru.aif.aifback.services.user.BotService;
import ru.aif.aifback.services.user.UserBotService;

/**
 * TG Admin API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgAdminService implements TgService {

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
     * @return true/false
     */
    @Override
    public Boolean process(TgWebhookRequest webhookRequest) {
        if (webhookRequest.isCallback()) {
            processCallback(webhookRequest);
        } else {
            processNoCallback(webhookRequest);
        }

        return Boolean.TRUE;
    }

    /**
     * Callback process.
     * @param webhookRequest webhook request
     */
    @Override
    public void processCallback(TgWebhookRequest webhookRequest) {
        try {
            String answer = null;
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

            if (Objects.equals(webhookRequest.getText(), BACK_TO_MAIN_MENU)) {
                answer = MENU_TITLE;
                keyboard = createMainMenuKeyboard();
            }

            if (webhookRequest.getText().contains(BOT_SELECT)) {
                answer = MENU_TITLE;
                processBotSelect(webhookRequest.getText(), keyboard);
                keyboard.addRow(TgAdminButtons.createBackButton(BACK_TO_MY_BOTS_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), BUY_BOT) || Objects.equals(webhookRequest.getText(), BACK_TO_BUY_BOTS_MENU)) {
                answer = SELECT_BOT_TITLE;
                processBuyBot(keyboard);
                keyboard.addRow(TgAdminButtons.createBackButton(BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), MY_BOTS)
                || Objects.equals(webhookRequest.getText(), BACK_TO_MY_BOTS_MENU)
                || webhookRequest.getText().contains(BOT_DELETE)
                || webhookRequest.getText().contains(BOT_CREATE)) {
                answer = processUserBot(webhookRequest.getChatId(), webhookRequest.getText(), keyboard);
                keyboard.addRow(TgAdminButtons.createBackButton(BACK_TO_MAIN_MENU));
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
            answer = userBotService.createUserBot(id, botId);
        }

        List<UserBot> userBots = userBotService.getUserBotsByTgId(id);
        if (!userBots.isEmpty()) {
            userBots.forEach(userBot -> {
                keyboard.addRow(new InlineKeyboardButton(
                        String.format("%s %s (ID: %s)",
                                      (userBot.isActive() && Objects.nonNull(userBot.getToken()) ? "✅" : "❌"),
                                      userBot.getBot().getDescription(),
                                      userBot.getId())).callbackData(String.format("%s;%s", BOT_SELECT, userBot.getId())));
            });

            answer = Objects.isNull(answer) ? MENU_TITLE : answer;
        } else {
            answer = Objects.isNull(answer) ? BOTS_EMPTY_TITLE : answer;
        }

        return answer;
    }

    /**
     * No callback process.
     * @param webhookRequest webhook request
     */
    @Override
    public void processNoCallback(TgWebhookRequest webhookRequest) {
        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), MENU_TITLE, TgAdminButtons.createMainMenuKeyboard(), bot);
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

        bots.forEach(bot -> {
            keyboard.addRow(new InlineKeyboardButton("✅ " + bot.getDescription()).callbackData(String.format("%s;%s", BOT_CREATE, bot.getId())));
        });
    }

    /**
     * Process bot select button.
     * @param text text
     * @param keyboard keyboard
     */
    private void processBotSelect(String text, InlineKeyboardMarkup keyboard) {
        if (!text.contains(DELIMITER)) {
            return;
        }

        String userBotId = text.split(DELIMITER)[1];
        userBotService.getUserBot(Long.valueOf(userBotId)).ifPresent(entity -> {
            if (Objects.isNull(entity.getToken())) {
                keyboard.addRow(TgAdminButtons.createLinkBotButton(userBotId));
            } else {
                keyboard.addRow(TgAdminButtons.createSelectedBotMenu(userBotId));
                keyboard.addRow(TgAdminButtons.createCalendarBotButton(userBotId));
            }
        });

        keyboard.addRow(TgAdminButtons.createDeleteBotButton(userBotId));
    }

    /**
     * Link bot.
     * @param id id
     * @param token token
     * @return true/false
     */
    public boolean linkBot(String id, String token) {
        return userBotService.linkBot(Long.valueOf(id), token);
    }

}
