package ru.aif.aifback.model.user;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * User staff item model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_user_staff_items")
@RequiredArgsConstructor
public class UserStaffItem {

    @Id
    private Long id;
    private Long aifUserStaffId;
    private Long aifUserItemId;
    private boolean active;
    private LocalDateTime created;

    @Transient
    private UserItem userItem;

}
