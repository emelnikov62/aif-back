package ru.aif.aifback.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * User bot model.
 * @author emelnikov
 */
@ToString
@Builder
@Data
public class UserBot {

    private Long id;
    private Long aifUserId;
    private Long aifBotId;
    private boolean active;
    private String token;
    private LocalDateTime created;
}
