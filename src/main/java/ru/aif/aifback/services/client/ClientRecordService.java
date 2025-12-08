package ru.aif.aifback.services.client;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.services.process.admin.enums.AdminStatsType.MONTH;
import static ru.aif.aifback.services.process.admin.enums.AdminStatsType.YEAR;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.CANCEL;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.FINISHED;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.user.NameWithCount;
import ru.aif.aifback.repository.client.ClientRecordRepository;
import ru.aif.aifback.services.process.admin.enums.AdminStatsType;
import ru.aif.aifback.services.process.client.enums.ClientRecordType;
import ru.aif.aifback.services.user.UserBotService;
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
    private final UserBotService userBotService;
    private final ClientService clientService;

    /**
     * Get client record by id.
     * @param id id
     * @return client record data
     */
    public ClientRecord getClientRecordById(Long id) {
        ClientRecord record = clientRecordRepository.findById(id).orElse(null);
        if (Objects.isNull(record)) {
            return null;
        }

        record.setUserItem(userItemService.findUserItemById(record.getAifUserItemId()).orElse(null));
        record.setUserStaff(userStaffService.getUserStaffById(record.getAifUserStaffId()));
        record.setUserCalendar(userCalendarService.findById(record.getAifUserCalendarId()).orElse(null));
        record.setClient(clientService.findById(record.getAifClientId()));
        record.setUserBot(userBotService.findById(record.getAifUserBotId()));

        return record;
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
     * @param recordId record id
     * @return id
     */
    public Optional<Long> addClientRecord(Long clientId, Long userBotId, Long userItemId, Long userCalendarId, Long userStaffId, Long hours,
                                          Long mins, Long recordId) {
        try {
            ClientRecord clientRecord;

            if (Objects.nonNull(recordId)) {
                clientRecord = clientRecordRepository.findById(recordId).orElse(null);

                if (Objects.isNull(clientRecord)) {
                    return Optional.empty();
                }

                clientRecord.setHours(hours);
                clientRecord.setMins(mins);
                clientRecord.setAifUserCalendarId(userCalendarId);
                clientRecord.setAifUserStaffId(userStaffId);
                clientRecord.setStatus(ClientRecordType.ACTIVE.getType());
                clientRecord.setCreated(LocalDateTime.now());
            } else {
                clientRecord = new ClientRecord(clientId,
                                                userBotId,
                                                userItemId,
                                                userCalendarId,
                                                userStaffId,
                                                hours,
                                                mins,
                                                ClientRecordType.ACTIVE.getType(),
                                                LocalDateTime.now());
            }

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
     * Find all completed by client id.
     * @param clientId client id
     * @return client records
     */
    public List<ClientRecord> findAllCompletedByClientId(Long clientId) {
        List<ClientRecord> records = clientRecordRepository.findAllCompletedByClientId(clientId);

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

    /**
     * Find all by period.
     * @param period period
     * @param userBotId user bot id
     * @return client records
     */
    public List<ClientRecord> findByPeriod(AdminStatsType period, Long userBotId) {
        try {
            LocalDateTime startDate = LocalDateTime.of(1900, 1, 1, 0, 0);
            if (Objects.equals(period, MONTH)) {
                startDate = LocalDateTime.of(2025, LocalDate.now().getMonth(), 1, 0, 0);
            }

            if (Objects.equals(period, YEAR)) {
                startDate = LocalDateTime.of(LocalDate.now().getYear(), 1, 1, 0, 0);
            }

            List<ClientRecord> records = clientRecordRepository.findByPeriod(userBotId, startDate);
            if (records.isEmpty()) {
                return Collections.emptyList();
            }

            records.forEach(record -> {
                record.setUserItem(userItemService.findUserItemById(record.getAifUserItemId()).orElse(null));
                record.setUserStaff(userStaffService.getUserStaffById(record.getAifUserStaffId()));
                record.setUserCalendar(userCalendarService.findById(record.getAifUserCalendarId()).orElse(null));
            });

            return records;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Find all for completed.
     * @param status status
     * @return client records
     */
    public List<ClientRecord> findAllForCompleted(String status) {
        List<ClientRecord> records = clientRecordRepository.findAllForCompleted(status);

        records.forEach(record -> {
            record.setUserBot(userBotService.findById(record.getAifUserBotId()));
            record.setClient(clientService.findById(record.getAifClientId()));
            record.setUserItem(userItemService.findUserItemById(record.getAifUserItemId()).orElse(null));
            record.setUserStaff(userStaffService.getUserStaffById(record.getAifUserStaffId()));
            record.setUserCalendar(userCalendarService.findById(record.getAifUserCalendarId()).orElse(null));

        });

        return records;
    }

    /**
     * Complete service.
     * @param id id
     * @return true/false
     */
    public Boolean completeService(Long id) {
        try {
            clientRecordRepository.completeService(id, FINISHED.getType());
            return TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return FALSE;
        }
    }

    /**
     * Find years records by status.
     * @param userBotId user bot id
     * @param status status
     * @return list years
     */
    public List<NameWithCount> findYearsRecordsByStatus(Long userBotId, String status) {
        return clientRecordRepository.findYearsRecordsByStatus(userBotId, status);
    }

    /**
     * Find months records by status.
     * @param userBotId user bot id
     * @param year year
     * @param status status
     * @return list months
     */
    public List<NameWithCount> findMonthsRecordsByStatus(Long userBotId, Long year, String status) {
        return clientRecordRepository.findMonthsRecordsByStatus(userBotId, year, status);
    }

    /**
     * Find days records by status.
     * @param userBotId user bot id
     * @param year year
     * @param month month
     * @param status status
     * @return list days
     */
    public List<NameWithCount> findDaysRecordsByStatus(Long userBotId, Long year, Long month, String status) {
        return clientRecordRepository.findDaysRecordsByStatus(userBotId, year, month, status);
    }

    /**
     * Get client record by date.
     * @param day day
     * @param month month
     * @param year year
     * @param userBotId user bot id
     * @param status status
     * @return client record data by date
     */
    public List<ClientRecord> findByDate(Long day, Long month, Long year, Long userBotId, String status) {
        List<ClientRecord> records = clientRecordRepository.findByDate(day, month, year, userBotId, status);
        if (records.isEmpty()) {
            return null;
        }

        records.forEach(record -> {
            record.setUserItem(userItemService.findUserItemById(record.getAifUserItemId()).orElse(null));
            record.setUserStaff(userStaffService.getUserStaffById(record.getAifUserStaffId()));
            record.setUserCalendar(userCalendarService.findById(record.getAifUserCalendarId()).orElse(null));
        });

        return records;
    }

}
