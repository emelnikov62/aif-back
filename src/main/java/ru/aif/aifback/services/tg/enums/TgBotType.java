package ru.aif.aifback.services.tg.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Bot type.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum TgBotType {

    BOT_ADMIN("admin"),
    BOT_RECORD("recording_clients");

    private final String type;

}
