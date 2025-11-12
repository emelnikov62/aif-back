package ru.aif.aifback.services.tg;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.UserBot;
import ru.aif.aifback.model.WebhookRequest;
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
    public Boolean process(WebhookRequest webhookRequest) {
        Optional<UserBot> userBot = userBotService.getUserBot(Long.valueOf(webhookRequest.getId()));
        if (userBot.isEmpty()) {
            return Boolean.FALSE;
        }

        TelegramBot bot = new TelegramBot(userBot.get().getToken());

        if (webhookRequest.isCallback()) {
            processCallback(webhookRequest.getChatId(), webhookRequest.getText(), bot);
        } else {
            processNoCallback(webhookRequest.getChatId(), webhookRequest.getText(), bot);
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
        sendMessage(Long.valueOf(id), text, bot);
    }

    /**
     * No callback process.
     * @param id id
     * @param text text
     * @param bot bot
     */
    public void processNoCallback(String id, String text, TelegramBot bot) {
        sendMessage(Long.valueOf(id), text, bot);
    }

    /**
     * Send message.
     * @param id id
     * @param text text
     * @param bot bot
     */
    public void sendMessage(Long id, String text, TelegramBot bot) {
        log.info("{}", bot.execute(new SendMessage(id, text)));
    }

    /**
     * Send message with keyboard.
     * @param id id
     * @param text text
     * @param keyboard keyboard
     * @param bot bot
     */
    public void sendMessage(Long id, String text, Keyboard keyboard, TelegramBot bot) {
        log.info("{}", bot.execute(new SendMessage(id, text).replyMarkup(keyboard)));
    }

}
