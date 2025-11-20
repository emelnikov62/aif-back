package ru.aif.aifback.model.client;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Client record model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_client_records")
@RequiredArgsConstructor
public class ClientRecord {

    @Id
    private Long id;
    private Long aifClientId;
    private Long aifUserBotId;
    private Long aifUserItemId;
    private Long aifUserCalendarId;
    private Long hours;
    private Long mins;
    private String status;
    private LocalDateTime created;

    public ClientRecord(Long aifClientId, Long aifUserBotId, Long aifUserItemId, Long aifUserCalendarId, Long hours, Long mins, String status,
                        LocalDateTime created) {
        this.aifClientId = aifClientId;
        this.aifUserBotId = aifUserBotId;
        this.aifUserItemId = aifUserItemId;
        this.aifUserCalendarId = aifUserCalendarId;
        this.hours = hours;
        this.mins = mins;
        this.status = status;
        this.created = created;
    }

}
