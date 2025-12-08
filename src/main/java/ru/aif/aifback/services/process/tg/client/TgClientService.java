package ru.aif.aifback.services.process.tg.client;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.ProcessService;
import ru.aif.aifback.services.process.BotProcessService;
import ru.aif.aifback.services.user.UserBotService;

/**
 * TG Client API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgClientService implements ProcessService {

    private final UserBotService userBotService;
    private final List<BotProcessService> botServiceList;

    /**
     * Process bot logic.
     * @param webhookRequest webhookAdminRequest
     * @return true/false
     */
    @Override
    public Boolean process(WebhookRequest webhookRequest) {
        Optional<UserBot> userBot = getUserBot(webhookRequest.getId());
        if (userBot.isEmpty()) {
            return Boolean.FALSE;
        }

        Optional<BotProcessService> tgBotService = botServiceList.stream()
                                                                 .filter(f -> Objects.equals(f.getBotType().getType(), userBot.get().getBot().getType()))
                                                                 .findFirst();
        if (tgBotService.isEmpty()) {
            return Boolean.FALSE;
        }

        return tgBotService.get().process(webhookRequest, userBot.get());
    }

    /**
     * Get user bot by id.
     * @param id id
     * @return user bot
     */
    private Optional<UserBot> getUserBot(String id) {
        return userBotService.getUserBot(Long.valueOf(id));
    }
}
