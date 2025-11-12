package ru.aif.aifback.model;

import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Table;

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
@Table("aif_user_bots")
public class UserBot {

    private Long id;
    private Long aifUserId;
    private Long aifBotId;
    private boolean active;
    private String token;
    private LocalDateTime created;
}
