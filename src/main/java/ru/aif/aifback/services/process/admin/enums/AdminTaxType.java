package ru.aif.aifback.services.process.admin.enums;

import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Admin tax type.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum AdminTaxType {

    BASE("base", "Базовая", "\uD83D\uDD36", "30 дней: <b>999</b>₽", "90 дней: <b>2 699</b>₽", "365 дней: <b>9 999</b>₽"),
    ADDITIONAL("additional", "Расширенная", "\uD83D\uDD37", "30 дней: <b>1 999</b>₽", "90 дней: <b>5 399</b>₽", "365 дней: <b>19 999</b>₽"),
    PREMIUM("premium", "Премиум", "⭐", "30 дней: <b>3 999</b>₽", "90 дней: <b>10 999</b>₽", "365 дней: <b>40 999</b>₽");

    private final String type;
    private final String name;
    private final String icon;
    private final String onePrice;
    private final String threePrice;
    private final String twelvePrice;

    public static AdminTaxType findByType(String type) {
        return Arrays.stream(values()).filter(v -> Objects.equals(type, v.getType())).findFirst().orElse(BASE);
    }

}
