package ru.aif.aifback.services.tg;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.constants.Constants.TG_TOKEN_ADMIN;
import static ru.aif.aifback.services.tg.TgAdminButtons.BACK_TO_BUY_BOTS_MENU;
import static ru.aif.aifback.services.tg.TgAdminButtons.BACK_TO_MAIN_MENU;
import static ru.aif.aifback.services.tg.TgAdminButtons.BACK_TO_MY_BOTS_MENU;
import static ru.aif.aifback.services.tg.TgAdminButtons.BOTS_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.TgAdminButtons.BOT_CREATE;
import static ru.aif.aifback.services.tg.TgAdminButtons.BOT_DELETE;
import static ru.aif.aifback.services.tg.TgAdminButtons.BOT_SELECT;
import static ru.aif.aifback.services.tg.TgAdminButtons.BUY_BOT;
import static ru.aif.aifback.services.tg.TgAdminButtons.MENU_TITLE;
import static ru.aif.aifback.services.tg.TgAdminButtons.MY_BOTS;
import static ru.aif.aifback.services.tg.TgAdminButtons.SELECT_BOT_TITLE;
import static ru.aif.aifback.services.tg.TgAdminButtons.createMainMenuKeyboard;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.Bot;
import ru.aif.aifback.model.UserBot;
import ru.aif.aifback.model.WebhookRequest;
import ru.aif.aifback.services.user.BotService;
import ru.aif.aifback.services.user.UserBotService;

/**
 * TG Admin API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgAdminService {

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
    public Boolean process(WebhookRequest webhookRequest) {
        if (webhookRequest.isCallback()) {
            processCallback(webhookRequest.getChatId(), webhookRequest.getText());
        } else {
            processNoCallback(webhookRequest.getChatId());
        }

        return Boolean.TRUE;
    }

    /**
     * Callback process.
     * @param id id
     * @param text text
     */
    public void processCallback(String id, String text) {
        try {
            String answer = null;
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

            if (Objects.equals(text, BACK_TO_MAIN_MENU)) {
                answer = MENU_TITLE;
                keyboard = createMainMenuKeyboard();
            }

            if (text.contains(BOT_SELECT)) {
                answer = MENU_TITLE;
                processBotSelect(text, keyboard);
                keyboard.addRow(TgAdminButtons.createBackButton(BACK_TO_MY_BOTS_MENU));
            }

            if (Objects.equals(text, BUY_BOT) || Objects.equals(text, BACK_TO_BUY_BOTS_MENU)) {
                answer = SELECT_BOT_TITLE;
                processBuyBot(keyboard);
                keyboard.addRow(TgAdminButtons.createBackButton(BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(text, MY_BOTS)
                || Objects.equals(text, BACK_TO_MY_BOTS_MENU)
                || text.contains(BOT_DELETE)
                || text.contains(BOT_CREATE)) {
                answer = processUserBot(id, text, keyboard);
                keyboard.addRow(TgAdminButtons.createBackButton(BACK_TO_MAIN_MENU));
            }

            if (Objects.isNull(answer)) {
                sendMessage(Long.valueOf(id), MENU_TITLE);
            } else {
                sendMessage(Long.valueOf(id), answer, keyboard);
            }
        } catch (Exception e) {
            sendMessage(TG_LOG_ID, e.getMessage());
        }
    }

    /**
     * Process user bots.
     * @param id id
     * @param text text
     * @param keyboard keyboard
     */
    public String processUserBot(String id, String text, InlineKeyboardMarkup keyboard) {
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
     * @param id id
     */
    public void processNoCallback(String id) {
        sendMessage(Long.valueOf(id), MENU_TITLE, TgAdminButtons.createMainMenuKeyboard());
    }

    /**
     * Process buy bot.
     * @param keyboard keyboard
     */
    public void processBuyBot(InlineKeyboardMarkup keyboard) {
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
    public void processBotSelect(String text, InlineKeyboardMarkup keyboard) {
        if (!text.contains(DELIMITER)) {
            return;
        }

        String userBotId = text.split(DELIMITER)[1];
        userBotService.getUserBot(Long.valueOf(userBotId)).ifPresent(entity -> {
            if (Objects.isNull(entity.getToken())) {
                keyboard.addRow(TgAdminButtons.createLinkBotButton(userBotId));
            } else {
                keyboard.addRow(TgAdminButtons.createSelectedBotMenu(userBotId));
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

    /**
     * Send message.
     * @param id id
     * @param text text
     */
    public void sendMessage(Long id, String text) {
        log.info("{}", bot.execute(new SendMessage(id, text)));
    }

    /**
     * Send message with keyboard.
     * @param id id
     * @param text text
     * @param keyboard keyboard
     */
    public void sendMessage(Long id, String text, Keyboard keyboard) {
        log.info("{}", bot.execute(new SendMessage(id, text).replyMarkup(keyboard)));
    }

}
