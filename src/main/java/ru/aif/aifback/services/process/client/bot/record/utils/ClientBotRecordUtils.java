package ru.aif.aifback.services.process.client.bot.record.utils;

import static ru.aif.aifback.enums.BotType.BOT_RECORD;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.ACTIVE_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.AI_RECORD_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.BACK_BUTTON_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.HISTORY_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.ITEMS_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.MENU_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.SETTINGS_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_AI_RECORD;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_GROUP;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_MAIN;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_SETTINGS;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType.ACTIVE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType.NO_ACTIVE;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ru.aif.aifback.model.message.ChatMessage;

/**
 * Client bot record utils.
 * @author emelnikov
 */
public final class ClientBotRecordUtils {

    /**
     * Create main menu.
     * @return main menu
     */
    public static List<List<ChatMessage.Button>> createMainMenuKeyboard(String typeBot) {
        if (Objects.equals(typeBot, BOT_RECORD.getType())) {
            return List.of(
                    List.of(ChatMessage.Button.builder().title(AI_RECORD_TITLE).callback(BOT_AI_RECORD.getType()).build()),
                    List.of(ChatMessage.Button.builder().title(ACTIVE_TITLE).callback(String.format("%s;%s", BOT_RECORDS.getType(), ACTIVE.getType()))
                                              .build()),
                    List.of(ChatMessage.Button.builder().title(ITEMS_TITLE).callback(BOT_GROUP.getType()).build()),
                    List.of(ChatMessage.Button.builder().title(HISTORY_TITLE)
                                              .callback(String.format("%s;%s", BOT_RECORDS.getType(), NO_ACTIVE.getType()))
                                              .build()),
                    List.of(ChatMessage.Button.builder().title(SETTINGS_TITLE).callback(BOT_SETTINGS.getType()).build())
            );
        }

        return Collections.emptyList();
    }

    /**
     * Create back button.
     * @param callback callback
     * @return back button
     */
    public static List<ChatMessage.Button> createBackButton(String callback) {
        return List.of(
                ChatMessage.Button.builder().title(BACK_BUTTON_TITLE).callback(callback).build(),
                ChatMessage.Button.builder().title(MENU_TITLE).callback(BOT_MAIN.getType()).build()
        );
    }

}
