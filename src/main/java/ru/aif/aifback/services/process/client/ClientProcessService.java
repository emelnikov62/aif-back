package ru.aif.aifback.services.process.client;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.ProcessService;
import ru.aif.aifback.services.process.client.bot.ClientBotProcessService;
import ru.aif.aifback.services.user.UserBotService;

/**
 * Client API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("clientProcessService")
public class ClientProcessService implements ProcessService {

    private final UserBotService userBotService;
    private final List<ClientBotProcessService> clientBotProcessServices;

    /**
     * Process bot logic.
     * @param webhookRequest webhookAdminRequest
     * @return true/false
     */
    @Override
    public Boolean process(WebhookRequest webhookRequest) {
        UserBot userBot = getUserBot(webhookRequest.getId());
        if (Objects.isNull(userBot)) {
            return Boolean.FALSE;
        }

        ClientBotProcessService botService = clientBotProcessServices.stream()
                                                                     .filter(f -> Objects.equals(f.getBotType().getType(),
                                                                                                 userBot.getBot().getType()))
                                                                     .filter(f -> Objects.equals(f.getSourceType().getSource(),
                                                                                                 userBot.getSource()))
                                                                     .findFirst()
                                                                     .orElse(null);
        if (Objects.isNull(botService)) {
            return Boolean.FALSE;
        }

        return botService.process(webhookRequest, userBot);
    }

    /**
     * Get user bot by id.
     * @param id id
     * @return user bot
     */
    private UserBot getUserBot(String id) {
        return userBotService.getUserBot(Long.valueOf(id)).orElse(null);
    }
}
