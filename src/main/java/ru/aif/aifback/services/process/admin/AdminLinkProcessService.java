package ru.aif.aifback.services.process.admin;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.services.process.ProcessService;
import ru.aif.aifback.services.user.UserBotService;

/**
 * Admin link process API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("adminLinkProcessService")
public class AdminLinkProcessService implements ProcessService {

    private final UserBotService userBotService;

    /**
     * Webhook process link bot.
     * @param webhookRequest webhookAdminRequest
     * @return true/false
     */
    @Override
    public Boolean process(WebhookRequest webhookRequest) {
        return userBotService.linkBot(Long.valueOf(webhookRequest.getId()), webhookRequest.getToken());
    }

}
