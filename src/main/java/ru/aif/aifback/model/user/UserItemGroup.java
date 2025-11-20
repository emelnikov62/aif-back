package ru.aif.aifback.model.user;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * User group item model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_user_item_groups")
@RequiredArgsConstructor
public class UserItemGroup {

    @Id
    private Long id;
    private Long aifUserBotId;
    private String name;
    private boolean active;
    private LocalDateTime created;

    @Transient
    private List<UserItem> items;

    public UserItemGroup(String name) {
        this.name = name;
    }

    public UserItemGroup(String name, Long aifUserBotId) {
        this.name = name;
        this.aifUserBotId = aifUserBotId;
    }

}
