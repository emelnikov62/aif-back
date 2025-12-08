package ru.aif.aifback.enums;

import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Bot source.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum BotSource {

    TELEGRAM("telegram"),
    MAX("max");

    private final String source;

    public static BotSource findByType(String type) {
        return Arrays.stream(values()).filter(v -> Objects.equals(type, v.getSource())).findFirst().orElse(TELEGRAM);
    }
}
