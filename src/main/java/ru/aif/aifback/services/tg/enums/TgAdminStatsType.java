package ru.aif.aifback.services.tg.enums;

import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Admin stats type.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum TgAdminStatsType {

    ALL("all", "\uD83D\uDCC5 Общая"),
    MONTH("month", "📅 Текущий месяц"),
    YEAR("year", "📅 Текущий год");

    private final String type;
    private final String name;

    public static TgAdminStatsType findByType(String type) {
        return Arrays.stream(values()).filter(v -> Objects.equals(type, v.getType())).findFirst().orElse(ALL);
    }

}
