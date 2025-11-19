package ru.aif.aifback.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * User calendar model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_user_calendar")
@RequiredArgsConstructor
public class UserCalendar {

    @Id
    private Long id;
    private Long aifUserBotId;
    private Long hoursStart;
    private Long minsStart;
    private Long hoursEnd;
    private Long minsEnd;
    private Long day;
    private Long month;
    private Long year;
    private LocalDateTime created;

}
