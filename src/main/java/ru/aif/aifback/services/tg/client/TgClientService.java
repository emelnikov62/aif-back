package ru.aif.aifback.services.tg.client;

import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.services.tg.admin.TgAdminButtons.BACK_TO_MAIN_MENU;
import static ru.aif.aifback.services.tg.client.TgClientButtons.BOT_ACTIVE;
import static ru.aif.aifback.services.tg.client.TgClientButtons.BOT_GROUP;
import static ru.aif.aifback.services.tg.client.TgClientButtons.BOT_HISTORY;
import static ru.aif.aifback.services.tg.client.TgClientButtons.BOT_SETTINGS;
import static ru.aif.aifback.services.tg.client.TgClientButtons.GROUP_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.client.TgClientButtons.MENU_TITLE;

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
import ru.aif.aifback.services.tg.TgService;
import ru.aif.aifback.services.tg.TgUtils;
import ru.aif.aifback.services.tg.admin.TgAdminButtons;
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
public class TgClientService implements TgService {

    private final UserBotService userBotService;
    private final UserItemService userItemService;
    private final BotService botService;

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

    private Optional<UserBot> getUserBot(String id) {
        Optional<UserBot> userBot = userBotService.getUserBot(Long.valueOf(id));
        if (userBot.isEmpty()) {
            return Optional.empty();
        }

        Optional<Bot> botType = botService.findById(userBot.get().getAifBotId());
        if (botType.isEmpty()) {
            return Optional.empty();
        }

        userBot.get().setBot(botType.get());
        return userBot;
    }

    /**
     * Callback process.
     * @param webhookRequest webhook request
     */
    @Override
    public void processCallback(TgWebhookRequest webhookRequest) {
        UserBot userBot = getUserBot(webhookRequest.getId()).orElse(null);
        if (Objects.isNull(userBot)) {
            return;
        }

        TelegramBot bot = new TelegramBot(userBot.getToken());
        try {
            String answer = null;
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

            if (Objects.equals(webhookRequest.getText(), BOT_ACTIVE)) {
                answer = MENU_TITLE;
                keyboard.addRow(TgClientButtons.createBackButton(TgClientButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), BOT_GROUP)) {
                answer = MENU_TITLE;
                if (!processBotGroups(userBot, keyboard)) {
                    answer = GROUP_EMPTY_TITLE;
                }
                keyboard.addRow(TgClientButtons.createBackButton(TgClientButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), BOT_HISTORY)) {
                answer = MENU_TITLE;
                keyboard.addRow(TgClientButtons.createBackButton(TgClientButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), BOT_SETTINGS)) {
                answer = MENU_TITLE;
                keyboard.addRow(TgClientButtons.createBackButton(TgClientButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), BACK_TO_MAIN_MENU)) {
                answer = MENU_TITLE;
                keyboard = TgClientButtons.createMainMenuKeyboard(userBot.getBot().getType());
            }

            if (Objects.isNull(answer)) {
                TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), TgAdminButtons.MENU_TITLE, bot);
            } else {
                TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), answer, keyboard, bot);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TgUtils.sendMessage(TG_LOG_ID, e.getMessage(), bot);
        }
    }

    /**
     * No callback process.
     * @param webhookRequest webhook request
     */
    @Override
    public void processNoCallback(TgWebhookRequest webhookRequest) {
        UserBot userBot = getUserBot(webhookRequest.getId()).orElse(null);
        if (Objects.isNull(userBot)) {
            return;
        }

        TelegramBot bot = new TelegramBot(userBot.getToken());
        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()),
                            MENU_TITLE,
                            TgClientButtons.createMainMenuKeyboard(userBot.getBot().getType()),
                            bot);
    }

    /**
     * Process bot groups button.
     * @param userBot user bot
     * @param keyboard keyboard
     * @return true/false
     */
    public boolean processBotGroups(UserBot userBot, InlineKeyboardMarkup keyboard) {
        List<UserItemGroup> groups = userItemService.getUserItemGroups(userBot.getId());
        if (groups.isEmpty()) {
            return Boolean.FALSE;
        }

        groups.forEach(group -> keyboard.addRow(TgClientButtons.createGroupsBotMenu(group)));

        return Boolean.TRUE;
    }

}
