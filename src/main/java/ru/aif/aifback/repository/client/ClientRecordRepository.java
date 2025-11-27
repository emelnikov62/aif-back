package ru.aif.aifback.repository.client;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.client.ClientRecord;

/**
 * Client record repository.
 * @author emelnikov
 */
@Repository
public interface ClientRecordRepository extends CrudRepository<ClientRecord, Long> {

    /**
     * Get client record by id.
     * @param id id
     * @return client record data
     */
    Optional<ClientRecord> findById(Long id);

    /**
     * Find all records by user bot id, calendar id, staff id
     * @param staffId staff id
     * @param calendarId calendar id
     * @param userBotId user bot id
     * @param status status
     * @return client records
     */
    @Query(value = "select r.*" +
                   "  from aif_client_records r" +
                   " where r.aif_user_calendar_id = :calendar_id" +
                   "   and r.aif_user_bot_id = :user_bot_id" +
                   "   and r.aif_user_staff_id = :staff_id" +
                   "   and r.status = :status" +
                   " order by r.hours")
    List<ClientRecord> findAllRecordsByStaffIdAndCalendarIdAndUserBotId(@Param("staff_id") Long staffId,
                                                                        @Param("calendar_id") Long calendarId,
                                                                        @Param("user_bot_id") Long userBotId,
                                                                        @Param("status") String status);

    /**
     * Find all by client id and status.
     * @param clientId client id
     * @param status status
     * @return client records
     */
    @Query(value = "select r.*" +
                   "  from aif_client_records r" +
                   "  join aif_user_calendar uc on uc.id = r.aif_user_calendar_id" +
                   " where r.aif_client_id = :client_id" +
                   "   and r.status = :status" +
                   " order by uc.day, uc.month, uc.year, r.hours, r.mins")
    List<ClientRecord> findAllByClientIdAndStatus(@Param("client_id") Long clientId, @Param("status") String status);

    /**
     * Find all completed by client id.
     * @param clientId client id
     * @return client records
     */
    @Query(value = "select r.*" +
                   "  from aif_client_records r" +
                   "  join aif_user_calendar uc on uc.id = r.aif_user_calendar_id" +
                   " where r.aif_client_id = :client_id" +
                   "   and r.status != 'active'" +
                   " order by uc.day, uc.month, uc.year, r.hours, r.mins")
    List<ClientRecord> findAllCompletedByClientId(@Param("client_id") Long clientId);

    /**
     * Cancel record.
     * @param id id
     * @param status status
     */
    @Query(value = "update aif_client_records set status = :status where id = :id")
    @Modifying
    void cancelRecord(@Param("id") Long id, @Param("status") String status);

    /**
     * Find all records by period.
     * @param userBotId user bot id
     * @param startDate start date
     * @return client records
     */
    @Query(value = "select r.*" +
                   "  from aif_client_records r" +
                   " where r.aif_user_bot_id = :user_bot_id" +
                   "   and r.created between :start_date and now()")
    List<ClientRecord> findByPeriod(@Param("user_bot_id") Long userBotId,
                                    @Param("start_date") LocalDateTime startDate);
}
