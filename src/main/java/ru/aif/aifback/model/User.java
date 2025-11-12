package ru.aif.aifback.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.ToString;

/**
 * User model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_users")
public class User {

    @Id
    private Long id;
    private String tgId;
    private boolean active;
    private LocalDateTime created;

    public User(String tgId) {
        this.tgId = tgId;
    }
}
