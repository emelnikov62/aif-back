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

    BASE("base", "Базовая", "\uD83D\uDD36", "1 месяц: <b>999\uD83D\uDCB2</b>", "3 месяца: <b>2 699\uD83D\uDCB2</b>", "1 год: <b>9 999\uD83D\uDCB2</b>"),
    ADDITIONAL("additional", "Расширенная", "\uD83D\uDD37", "1 месяц: <b>1 999\uD83D\uDCB2</b>", "3 месяца: <b>5 399\uD83D\uDCB2</b>", "1 год: <b>19 999\uD83D\uDCB2</b>"),
    PREMIUM("premium", "Премиум", "⭐", "1 месяц: <b>3 999\uD83D\uDCB2</b>", "3 месяца: <b>10 999\uD83D\uDCB2</b>", "1 год: <b>40 999\uD83D\uDCB2</b>");

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
