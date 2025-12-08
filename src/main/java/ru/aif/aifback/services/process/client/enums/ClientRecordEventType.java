package ru.aif.aifback.services.process.client.enums;

import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Client record event type.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum ClientRecordEventType {

    NEW("new", "Новая запись", "\uD83D\uDD35"),
    CANCEL("cancel", "Запись отменена", "❌"),
    FINISHED("finished", "Услуга оказана", "✅"),
    EDIT("edit", "Запись изменена", "\uD83D\uDCDD");

    private final String type;
    private final String name;
    private final String icon;

    public static ClientRecordEventType findByType(String type) {
        return Arrays.stream(values()).filter(v -> Objects.equals(type, v.getType())).findFirst().orElse(FINISHED);
    }

}
