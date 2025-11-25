package ru.aif.aifback.services.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.GROUP_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.GROUP_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.ITEMS_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_GROUP;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_ITEMS;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
import ru.aif.aifback.services.user.UserItemService;

/**
 * TG Item groups operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgItemGroupsOperationService implements TgClientBotOperationService {

    private final UserItemService userItemService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String answer = processBotGroups(userBot, keyboard);
        keyboard.addRow(createBackButton(BOT_MAIN.getType()));

        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
    }

    /**
     * Process bot groups button.
     * @param userBot user bot
     * @param keyboard keyboard
     * @return answer
     */
    private String processBotGroups(UserBot userBot, InlineKeyboardMarkup keyboard) {
        List<UserItemGroup> groups = userItemService.getUserItemGroupsAndActive(userBot.getId());
        if (groups.isEmpty()) {
            return GROUP_EMPTY_TITLE;
        }

        groups.forEach(group -> keyboard.addRow(new InlineKeyboardButton(String.format(GROUP_TITLE, group.getName()))
                                                        .callbackData(String.format("%s;%s", BOT_ITEMS.getType(), group.getId()))));
        return ITEMS_TITLE;
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return BOT_GROUP;
    }
}
