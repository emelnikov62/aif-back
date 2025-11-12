package ru.aif.aifback.services.tg;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.constants.Constants.TG_TOKEN_ADMIN;
import static ru.aif.aifback.services.tg.TgButtons.BACK_TO_BUY_BOTS_MENU;
import static ru.aif.aifback.services.tg.TgButtons.BACK_TO_MAIN_MENU;
import static ru.aif.aifback.services.tg.TgButtons.BACK_TO_MY_BOTS_MENU;
import static ru.aif.aifback.services.tg.TgButtons.BOTS_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.TgButtons.BOT_CREATE;
import static ru.aif.aifback.services.tg.TgButtons.BOT_DELETE;
import static ru.aif.aifback.services.tg.TgButtons.BOT_SELECT;
import static ru.aif.aifback.services.tg.TgButtons.BUY_BOT;
import static ru.aif.aifback.services.tg.TgButtons.MENU_TITLE;
import static ru.aif.aifback.services.tg.TgButtons.MY_BOTS;
import static ru.aif.aifback.services.tg.TgButtons.SELECT_BOT_TITLE;
import static ru.aif.aifback.services.tg.TgButtons.createMainMenuKeyboard;

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
import ru.aif.aifback.model.WebhookAdminRequest;
import ru.aif.aifback.services.user.BotService;
import ru.aif.aifback.services.user.UserBotService;

/**
 * TG API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgService {

    private final UserBotService userBotService;
    private final BotService botService;
    private TelegramBot bot;

    @PostConstruct
    void init() {
        bot = new TelegramBot(TG_TOKEN_ADMIN);
    }

    public Boolean process(WebhookAdminRequest webhookAdminRequest) {
        if (webhookAdminRequest.isCallback()) {
            processCallback(webhookAdminRequest.getChatId(), webhookAdminRequest.getText());
        } else {
            processNoCallback(webhookAdminRequest.getChatId());
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
                keyboard.addRow(TgButtons.createBackButton(BACK_TO_MY_BOTS_MENU));
            }

            if (Objects.equals(text, BUY_BOT) || Objects.equals(text, BACK_TO_BUY_BOTS_MENU)) {
                answer = SELECT_BOT_TITLE;
                processBuyBot(keyboard);
                keyboard.addRow(TgButtons.createBackButton(BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(text, MY_BOTS) || Objects.equals(text, BACK_TO_MY_BOTS_MENU)) {
                answer = processUserBot(id, text, keyboard);

                if (Objects.isNull(answer)) {
                    answer = BOTS_EMPTY_TITLE;
                }

                keyboard.addRow(TgButtons.createBackButton(BACK_TO_MAIN_MENU));
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
        String answer = MENU_TITLE;

        if (text.contains(BOT_DELETE)) {
            Long userBotId = Long.valueOf(text.split(DELIMITER)[1]);
            answer = userBotService.deleteUserBot(userBotId);
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

        }

        return answer;
    }

    /**
     * No callback process.
     * @param id id
     */
    public void processNoCallback(String id) {
        sendMessage(Long.valueOf(id), MENU_TITLE, TgButtons.createMainMenuKeyboard());
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
                keyboard.addRow(TgButtons.createLinkBotButton(userBotId));
            } else {
                keyboard.addRow(TgButtons.createSelectedBotMenu(userBotId));
            }
        });

        keyboard.addRow(TgButtons.createDeleteBotButton(userBotId));
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
