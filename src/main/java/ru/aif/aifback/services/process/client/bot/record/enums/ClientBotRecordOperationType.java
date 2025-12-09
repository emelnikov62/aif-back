package ru.aif.aifback.services.process.client.bot.record.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Client operation type bot.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum ClientBotRecordOperationType {

    BOT_RECORDS("bot_records"),
    BOT_HISTORY("bot_history"),
    BOT_SETTINGS("bot_settings"),
    BOT_GROUP("bot_group"),
    BOT_ITEMS("bot_items"),
    BOT_ITEM_ADDITIONAL("bot_item_additional"),
    BOT_MAIN("bot_main_menu"),
    BOT_SELECT_YEAR("bot_select_year"),
    BOT_SELECT_MONTH("bot_select_month"),
    BOT_SELECT_DAY("bot_select_day"),
    BOT_SELECT_TIME("bot_select_time"),
    BOT_CONFIRM_SELECT_TIME("bot_confirm_select_time"),
    BOT_ADD_RECORD("bot_add_record"),
    BOT_RECORD_SHOW("bot_record_show"),
    BOT_RECORD_CANCEL("bot_record_cancel"),
    BOT_RECORD_EDIT("bot_record_edit"),
    BOT_CLIENT_STAR("bot_client_star"),
    BOT_AI_RECORD("bot_ai_record"),
    BOT_AI_RECORD_PROCESS("bot_ai_process");

    private final String type;

}
