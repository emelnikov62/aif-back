package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.enums.BotType.BOT_RECORD;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_CALENDAR_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_ITEMS_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_RECORDS_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_STAFF_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_STATS_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.DELETE_BOT_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.LINK_TOKEN_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.MENU_TITLE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_DELETE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_SELECT;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_STATS;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.getBotIconByType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.admin.AdminBotOperationService;
import ru.aif.aifback.services.process.admin.enums.AdminBotOperationType;
import ru.aif.aifback.services.user.UserBotService;

/**
 * Admin Bot select operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotSelectOperationService implements AdminBotOperationService {

    private final UserBotService userBotService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        Long userBotId = Long.valueOf(webhookRequest.getText().split(DELIMITER)[1]);
        String answer = MENU_TITLE;
        List<ChatMessage.Button> buttons = new ArrayList<>();

        Optional<UserBot> userBot = userBotService.getUserBot(userBotId);
        if (userBot.isPresent()) {
            answer = String.format("%s %s (ID: %s)",
                                   getBotIconByType(userBot.get().getBot().getType()),
                                   userBot.get().getBot().getDescription(),
                                   userBot.get().getId());

            if (Objects.isNull(userBot.get().getToken())) {
                buttons.add(ChatMessage.Button.builder()
                                              .title(LINK_TOKEN_TITLE)
                                              .url("https://aif-back-emelnikov62.amvera.io/aif/admin/link-bot-form?id=" + userBotId)
                                              .isBack(FALSE)
                                              .build());
            } else {
                buttons.add(ChatMessage.Button.builder()
                                              .title(BOT_RECORDS_TITLE)
                                              .callback(String.format("%s;%s", BOT_RECORDS.getType(), userBotId))
                                              .isBack(FALSE)
                                              .build());

                buttons.add(ChatMessage.Button.builder()
                                              .title(BOT_STATS_TITLE)
                                              .callback(String.format("%s;%s", BOT_STATS.getType(), userBotId))
                                              .isBack(FALSE)
                                              .build());

                buttons.add(ChatMessage.Button.builder()
                                              .title(BOT_ITEMS_TITLE)
                                              .url("https://aif-back-emelnikov62.amvera.io/aif/admin/items-bot-form?id=" + userBotId)
                                              .isBack(FALSE)
                                              .build());

                if (Objects.equals(userBot.get().getBot().getType(), BOT_RECORD.getType())) {
                    buttons.add(ChatMessage.Button.builder()
                                                  .title(BOT_STAFF_TITLE)
                                                  .url("https://aif-back-emelnikov62.amvera.io/aif/admin/staff-bot-form?id=" + userBotId)
                                                  .isBack(FALSE)
                                                  .build());
                    buttons.add(ChatMessage.Button.builder()
                                                  .title(BOT_CALENDAR_TITLE)
                                                  .url("https://aif-back-emelnikov62.amvera.io/aif/admin/calendar-bot-form?id=" + userBotId)
                                                  .isBack(FALSE)
                                                  .build());
                }
            }

            buttons.add(ChatMessage.Button.builder()
                                          .title(DELETE_BOT_TITLE)
                                          .callback(String.format("%s;%s", BOT_DELETE.getType(), userBotId))
                                          .isBack(FALSE)
                                          .build());
        }

        buttons.addAll(createBackButton(BOT_MAIN.getType()));

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
        return BOT_SELECT;
    }
}
