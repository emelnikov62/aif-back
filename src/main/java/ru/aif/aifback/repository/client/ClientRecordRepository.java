package ru.aif.aifback.repository.client;

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
     * Add client record.
     * @param aifClientId client id
     * @param aifUserBotId user bot id
     * @param aifUserItemId user item id
     * @param aifUserCalendarId user calendar id
     * @param hours hours
     * @param mins mins
     * @return id
     */
    @Query(value = "insert into aif_client_records(aif_client_id, aif_user_bot_id, aif_user_item_id, aif_user_calendar_id, hours, mins)" +
                   "     values(:aif_client_id, :aif_user_bot_id, :aif_user_item_id, :aif_user_calendar_id, :hours, :mins)")
    @Modifying
    Long addClientRecord(@Param("aif_client_id") Long aifClientId,
                         @Param("aif_user_bot_id") Long aifUserBotId,
                         @Param("aif_user_item_id") Long aifUserItemId,
                         @Param("aif_user_calendar_id") Long aifUserCalendarId,
                         @Param("hours") Long hours,
                         @Param("mins") Long mins);

}
