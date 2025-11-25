package ru.aif.aifback.services.client;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.services.tg.enums.TgClientRecordType.CANCEL;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.repository.client.ClientRecordRepository;
import ru.aif.aifback.services.tg.enums.TgClientRecordType;
import ru.aif.aifback.services.user.UserCalendarService;
import ru.aif.aifback.services.user.UserItemService;
import ru.aif.aifback.services.user.UserStaffService;

/**
 * Client record API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class ClientRecordService {

    private final ClientRecordRepository clientRecordRepository;
    private final UserItemService userItemService;
    private final UserStaffService userStaffService;
    private final UserCalendarService userCalendarService;

    /**
     * Get client record by id.
     * @param id id
     * @return client record data
     */
    public ClientRecord getClientRecordById(Long id) {
        Optional<ClientRecord> record = clientRecordRepository.findById(id);
        if (record.isEmpty()) {
            return null;
        }

        record.get().setUserItem(userItemService.findUserItemById(record.get().getAifUserItemId()).orElse(null));
        record.get().setUserStaff(userStaffService.getUserStaffById(record.get().getAifUserStaffId()));
        record.get().setUserCalendar(userCalendarService.findById(record.get().getAifUserCalendarId()).orElse(null));

        return record.get();
    }

    /**
     * Create client record
     * @param clientId client id
     * @param userBotId user bot id
     * @param userItemId user item id
     * @param userCalendarId user calendar id
     * @param userStaffId user staff id
     * @param hours hours
     * @param mins mins
     * @return id
     */
    public Optional<Long> addClientRecord(Long clientId, Long userBotId, Long userItemId, Long userCalendarId, Long userStaffId, Long hours,
                                          Long mins) {
        try {
            ClientRecord clientRecord = new ClientRecord(clientId,
                                                         userBotId,
                                                         userItemId,
                                                         userCalendarId,
                                                         userStaffId,
                                                         hours,
                                                         mins,
                                                         TgClientRecordType.ACTIVE.getType(),
                                                         LocalDateTime.now());
            clientRecordRepository.save(clientRecord);

            return Optional.of(clientRecord.getId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Find all records by staff id, user bot id, calendar id
     * @param staffId staff id
     * @param calendarId calendar id
     * @param userBotId user bot id
     * @param status status
     * @return client records
     */
    public List<ClientRecord> findAllRecordsByStaffAndDayAndStatus(Long staffId, Long calendarId, Long userBotId, String status) {
        List<ClientRecord> records = clientRecordRepository.findAllRecordsByStaffIdAndCalendarIdAndUserBotId(staffId, calendarId, userBotId, status);
        records.forEach(record -> record.setUserItem(userItemService.findUserItemById(record.getAifUserItemId()).orElse(null)));

        return records;
    }

    /**
     * Find all by client id and status.
     * @param clientId client id
     * @param status status
     * @return client records
     */
    public List<ClientRecord> findAllByClientIdAndStatus(Long clientId, String status) {
        List<ClientRecord> records = clientRecordRepository.findAllByClientIdAndStatus(clientId, status);

        records.forEach(record -> {
            record.setUserItem(userItemService.findUserItemById(record.getAifUserItemId()).orElse(null));
            record.setUserStaff(userStaffService.getUserStaffById(record.getAifUserStaffId()));
            record.setUserCalendar(userCalendarService.findById(record.getAifUserCalendarId()).orElse(null));
        });

        return records;
    }

    /**
     * Cancel record.
     * @param id id
     * @return true/false
     */
    public Boolean cancelRecord(Long id) {
        try {
            clientRecordRepository.cancelRecord(id, CANCEL.getType());
            return TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return FALSE;
        }
    }

}
