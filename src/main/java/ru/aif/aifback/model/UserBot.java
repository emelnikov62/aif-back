package ru.aif.aifback.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * User bot model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_user_bots")
@RequiredArgsConstructor
public class UserBot {

    @Id
    private Long id;
    private Long aifUserId;
    private Long aifBotId;
    private boolean active;
    private String token;
    private LocalDateTime created;
    @Transient
    private Bot bot;

    public UserBot(Long id, Long aifUserId, Long aifBotId, boolean active, String token, LocalDateTime created) {
        this.id = id;
        this.aifUserId = aifUserId;
        this.aifBotId = aifBotId;
        this.active = active;
        this.token = token;
        this.created = created;
    }

    public UserBot(Long aifUserId, Long aifBotId) {
        this.aifUserId = aifUserId;
        this.aifBotId = aifBotId;
    }
}
