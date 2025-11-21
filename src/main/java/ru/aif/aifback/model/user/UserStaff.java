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
 * User staff model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_user_staffs")
@RequiredArgsConstructor
public class UserStaff {

    @Id
    private Long id;
    private Long aifUserBotId;
    private String name;
    private String surname;
    private String third;
    private boolean active;
    private LocalDateTime created;

    @Transient
    private List<UserStaffItem> items;

    public UserStaff(Long id, Long aifUserBotId, String name, String surname, String third, boolean active, LocalDateTime created) {
        this.id = id;
        this.aifUserBotId = aifUserBotId;
        this.name = name;
        this.surname = surname;
        this.third = third;
        this.active = active;
        this.created = created;
    }

    public UserStaff(Long aifUserBotId, String name, String surname, String third) {
        this.aifUserBotId = aifUserBotId;
        this.name = name;
        this.surname = surname;
        this.third = third;
    }

    public UserStaff(Long aifUserBotId, String name, String surname, String third, boolean active, LocalDateTime created) {
        this.aifUserBotId = aifUserBotId;
        this.name = name;
        this.surname = surname;
        this.third = third;
        this.active = active;
        this.created = created;
    }

}
