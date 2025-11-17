package ru.aif.aifback.services.tg;

import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.services.tg.TgAdminButtons.BACK_TO_MAIN_MENU;
import static ru.aif.aifback.services.tg.TgClientButtons.BOT_ACTIVE;
import static ru.aif.aifback.services.tg.TgClientButtons.BOT_GROUP;
import static ru.aif.aifback.services.tg.TgClientButtons.BOT_HISTORY;
import static ru.aif.aifback.services.tg.TgClientButtons.BOT_SETTINGS;
import static ru.aif.aifback.services.tg.TgClientButtons.GROUP_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.TgClientButtons.MENU_TITLE;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.Bot;
import ru.aif.aifback.model.UserBot;
import ru.aif.aifback.model.UserItemGroup;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.user.BotService;
import ru.aif.aifback.services.user.UserBotService;
import ru.aif.aifback.services.user.UserItemService;

/**
 * TG Client API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgClientService {

    private final UserBotService userBotService;
    private final UserItemService userItemService;
    private final BotService botService;

    /**
     * Webhook process.
     * @param webhookRequest webhookAdminRequest
     * @return true/false
     */
    public Boolean process(TgWebhookRequest webhookRequest) {
        Optional<UserBot> userBot = userBotService.getUserBot(Long.valueOf(webhookRequest.getId()));
        if (userBot.isEmpty()) {
            return Boolean.FALSE;
        }

        Optional<Bot> botType = botService.findById(userBot.get().getAifBotId());
        if (botType.isEmpty()) {
            return Boolean.FALSE;
        }

        userBot.get().setBot(botType.get());
        TelegramBot bot = new TelegramBot(userBot.get().getToken());

        if (webhookRequest.isCallback()) {
            processCallback(webhookRequest.getChatId(), webhookRequest.getText(), userBot.get(), bot);
        } else {
            processNoCallback(webhookRequest.getChatId(), MENU_TITLE, TgClientButtons.createMainMenuKeyboard(userBot.get().getBot().getType()), bot);
        }

        return Boolean.TRUE;
    }

    /**
     * Callback process.
     * @param id id
     * @param text text
     * @param userBot user bot
     * @param bot bot
     */
    public void processCallback(String id, String text, UserBot userBot, TelegramBot bot) {
        try {
            String answer = null;
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

            if (Objects.equals(text, BOT_ACTIVE)) {
                answer = MENU_TITLE;
                keyboard.addRow(TgClientButtons.createBackButton(TgClientButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(text, BOT_GROUP)) {
                answer = MENU_TITLE;
                if (!processBotGroups(text, userBot, keyboard)) {
                    answer = GROUP_EMPTY_TITLE;
                }
                keyboard.addRow(TgClientButtons.createBackButton(TgClientButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(text, BOT_HISTORY)) {
                answer = MENU_TITLE;
                keyboard.addRow(TgClientButtons.createBackButton(TgClientButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(text, BOT_SETTINGS)) {
                answer = MENU_TITLE;
                keyboard.addRow(TgClientButtons.createBackButton(TgClientButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(text, BACK_TO_MAIN_MENU)) {
                answer = MENU_TITLE;
                keyboard = TgClientButtons.createMainMenuKeyboard(userBot.getBot().getType());
            }

            if (Objects.isNull(answer)) {
                TgUtils.sendMessage(Long.valueOf(id), TgAdminButtons.MENU_TITLE, bot);
            } else {
                TgUtils.sendMessage(Long.valueOf(id), answer, keyboard, bot);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TgUtils.sendMessage(TG_LOG_ID, e.getMessage(), bot);
        }
    }

    /**
     * No callback process.
     * @param id id
     * @param text text
     * @param keyboard
     * @param bot bot
     */
    public void processNoCallback(String id, String text, InlineKeyboardMarkup keyboard, TelegramBot bot) {
        TgUtils.sendMessage(Long.valueOf(id), text, keyboard, bot);
    }

    /**
     * Process bot groups button.
     * @param text text
     * @param userBot user bot
     * @param keyboard keyboard
     * @return true/false
     */
    public boolean processBotGroups(String text, UserBot userBot, InlineKeyboardMarkup keyboard) {
        List<UserItemGroup> groups = userItemService.getUserItemGroups(userBot.getId());
        if (groups.isEmpty()) {
            return Boolean.FALSE;
        }

        groups.forEach(group -> {
            keyboard.addRow(TgClientButtons.createGroupsBotMenu(group));
        });

        return Boolean.TRUE;
    }

}
