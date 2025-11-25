package ru.aif.aifback.model.client;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserStaff;

/**
 * Client star model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_client_stars")
@RequiredArgsConstructor
public class ClientStar {

    @Id
    private Long id;
    private Long aifClientId;
    private Long aifUserBotId;
    private Long aifUserItemId;
    private Long aifUserStaffId;
    private Long value;
    private LocalDateTime created;

    @Transient
    private UserItem userItem;
    @Transient
    private UserCalendar userCalendar;
    @Transient
    private UserStaff userStaff;

    public ClientStar(Long aifClientId, Long aifUserBotId, Long aifUserItemId, Long aifUserStaffId, Long value, LocalDateTime created) {
        this.aifClientId = aifClientId;
        this.aifUserBotId = aifUserBotId;
        this.aifUserItemId = aifUserItemId;
        this.aifUserStaffId = aifUserStaffId;
        this.value = value;
        this.created = created;
    }

}
