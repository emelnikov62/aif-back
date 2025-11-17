package ru.aif.aifback.services.tg;

import static ru.aif.aifback.services.tg.TgClientButtons.MENU_TITLE;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.Bot;
import ru.aif.aifback.model.UserBot;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.user.BotService;
import ru.aif.aifback.services.user.UserBotService;

/**
 * TG Client API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgClientService {

    private final UserBotService userBotService;
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
            processCallback(webhookRequest.getChatId(), webhookRequest.getText(), bot);
        } else {
            processNoCallback(webhookRequest.getChatId(), MENU_TITLE, TgClientButtons.createMainMenuKeyboard(userBot.get().getBot().getType()), bot);
        }

        return Boolean.TRUE;
    }

    /**
     * Callback process.
     * @param id id
     * @param text text
     * @param bot bot
     */
    public void processCallback(String id, String text, TelegramBot bot) {
        TgUtils.sendMessage(Long.valueOf(id), text, bot);
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

}
