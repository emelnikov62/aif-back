package ru.aif.aifback.model.user;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * User model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_users")
@RequiredArgsConstructor
public class User {

    @Id
    private Long id;
    private String sourceId;
    private boolean active;
    private LocalDateTime created;
    private String source;

    public User(String sourceId, String source) {
        this.sourceId = sourceId;
        this.source = source;
    }

    public User(String sourceId, boolean active, LocalDateTime created, String source) {
        this.sourceId = sourceId;
        this.active = active;
        this.created = created;
        this.source = source;
    }
}
