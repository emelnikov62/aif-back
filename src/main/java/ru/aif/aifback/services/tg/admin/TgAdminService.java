package ru.aif.aifback.services.tg.admin;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.tg.TgService;
import ru.aif.aifback.services.tg.admin.bot.TgAdminBotService;
import ru.aif.aifback.services.user.UserBotService;

/**
 * TG Admin API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgAdminService implements TgService {

    private final TgAdminBotService tgBotAdminService;
    private final UserBotService userBotService;

    /**
     * Webhook process.
     * @param webhookRequest webhookAdminRequest
     * @return true/false
     */
    @Override
    public Boolean process(TgWebhookRequest webhookRequest) {
        return tgBotAdminService.process(webhookRequest, null);
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
