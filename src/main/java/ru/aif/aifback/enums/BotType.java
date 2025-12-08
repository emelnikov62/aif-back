package ru.aif.aifback.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Bot type.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum BotType {

    BOT_ADMIN("admin"),
    BOT_RECORD("recording_clients");

    private final String type;

}
