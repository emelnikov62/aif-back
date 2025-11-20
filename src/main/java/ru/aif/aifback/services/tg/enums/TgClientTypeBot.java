package ru.aif.aifback.services.tg.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Client type bot.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum TgClientTypeBot {

    BOT_ADMIN("admin"),
    BOT_RECORD("recording_clients");

    private final String type;

}
