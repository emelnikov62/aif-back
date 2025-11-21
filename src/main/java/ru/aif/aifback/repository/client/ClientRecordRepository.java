package ru.aif.aifback.repository.client;

import java.util.List;
import java.util.Optional;

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
                   " where r.aif_client_id = :client_id" +
                   "   and r.status = :status" +
                   " order by r.hours")
    List<ClientRecord> findAllByClientIdAndStatus(@Param("client_id") Long clientId, @Param("status") String status);

}
