package ru.aif.aifback.model.requests;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Calendaar request.
 * @author emelnikov
 */
@Builder
@ToString
@Data
@AllArgsConstructor
public class UserCalendarRequest {

    private Long id;

    private Long hoursStart;

    private Long minsStart;

    private Long hoursEnd;

    private Long minsEnd;

    private List<Long> days;

    private Long month;

    private Long year;

}
