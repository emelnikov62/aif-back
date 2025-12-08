package ru.aif.aifback.services.process.admin;

import static java.lang.Boolean.FALSE;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.services.process.ProcessService;
import ru.aif.aifback.services.process.admin.bot.AdminBotProcessService;

/**
 * Admin process API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("adminProcessService")
public class AdminProcessService implements ProcessService {

    private final List<AdminBotProcessService> adminBotProcessServices;

    /**
     * Webhook process.
     * @param webhookRequest webhookAdminRequest
     * @return true/false
     */
    @Override
    public Boolean process(WebhookRequest webhookRequest) {
        AdminBotProcessService processService = adminBotProcessServices.stream()
                                                                       .filter(f -> Objects.equals(f.getSourceType().getSource(),
                                                                                                   webhookRequest.getSource()))
                                                                       .findFirst()
                                                                       .orElse(null);
        if (Objects.isNull(processService)) {
            return FALSE;
        }

        return processService.process(webhookRequest, null);
    }

}
