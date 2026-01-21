package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_TAX;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_TAX_ADDITIONAL;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.services.process.admin.AdminBotOperationService;
import ru.aif.aifback.services.process.admin.enums.AdminBotOperationType;
import ru.aif.aifback.services.process.admin.enums.AdminTaxType;

/**
 * Admin Tax additional operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotTaxAdditionalOperationService implements AdminBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        Long userBotId = Long.valueOf(webhookRequest.getText().split(DELIMITER)[2]);
        String taxType = webhookRequest.getText().split(DELIMITER)[1];
        AdminTaxType adminTaxType = AdminTaxType.findByType(taxType);

        String answer = String.format("%s %s\n\n%s", adminTaxType.getIcon(), adminTaxType.getName(), adminTaxType.getDescription());

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();
        buttons.add(List.of(ChatMessage.Button.builder()
                                              .title(adminTaxType.getOnePrice())
                                              .url("https://aif-back-emelnikov62.amvera.io")
                                              .build()));
        buttons.add(List.of(ChatMessage.Button.builder()
                                              .title(adminTaxType.getThreePrice())
                                              .url("https://aif-back-emelnikov62.amvera.io")
                                              .build()));
        buttons.add(List.of(ChatMessage.Button.builder()
                                              .title(adminTaxType.getTwelvePrice())
                                              .url("https://aif-back-emelnikov62.amvera.io")
                                              .build()));

        buttons.add(createBackButton(String.format("%s;%s", BOT_TAX.getType(), userBotId)));

        return List.of(ChatMessage.builder()
                                  .text(answer)
                                  .updated(TRUE)
                                  .source(findByType(webhookRequest.getSource()))
                                  .chatId(webhookRequest.getChatId())
                                  .messageId(webhookRequest.getMessageId())
                                  .buttons(buttons)
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public AdminBotOperationType getOperationType() {
        return BOT_TAX_ADDITIONAL;
    }
}
