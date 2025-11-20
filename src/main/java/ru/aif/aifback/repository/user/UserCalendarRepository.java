package ru.aif.aifback.repository.user;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.user.UserCalendar;

/**
 * User calendar repository.
 * @author emelnikov
 */
@Repository
public interface UserCalendarRepository extends CrudRepository<UserCalendar, Long> {

    /**
     * Add day calendar.
     * @param day day
     * @param month month
     * @param year year
     * @param hoursStart hours start
     * @param minsStart mins start
     * @param hoursEnd hours end
     * @param minsEnd mins end
     * @param aifUserBotId user bot id
     * @return id
     */
    @Query(value = "insert into aif_user_calendar (day, month, year, hours_start, mins_start, hours_end, mins_end, aif_user_bot_id)" +
                   "     values(:day, :month, :year, :hours_start, :mins_start, :hours_end, :mins_end, :aif_user_bot_id)")
    @Modifying
    Long addDay(@Param("day") Long day,
                @Param("month") Long month,
                @Param("year") Long year,
                @Param("hours_start") Long hoursStart,
                @Param("mins_start") Long minsStart,
                @Param("hours_end") Long hoursEnd,
                @Param("mins_end") Long minsEnd,
                @Param("aif_user_bot_id") Long aifUserBotId);

    /**
     * Get user calendar.
     * @param aifUserBotId user bot id
     * @param month month
     * @param year year
     * @return user calendar
     */
    @Query(value = "select * from aif_user_calendar a where a.aif_user_bot_id = :aif_user_bot_id and a.month = :month and a.year = :year")
    List<UserCalendar> getUserCalendar(@Param("aif_user_bot_id") Long aifUserBotId,
                                       @Param("month") Long month,
                                       @Param("year") Long year);

    /**
     * Delete days from calendar.
     * @param ids ids
     * @param aifUserBotId user bot id
     */
    @Query(value = "delete from aif_user_calendar c where c.aif_user_bot_id = :aif_user_bot_id and c.id in (:ids)")
    @Modifying
    void deleteDays(@Param("ids") List<Long> ids, @Param("aif_user_bot_id") Long aifUserBotId);

    /**
     * Edit day.
     * @param id id
     * @param hoursStart hours start
     * @param minsStart mins start
     * @param hoursEnd hours end
     * @param minsEnd mins end
     */
    @Query(value = "update aif_user_calendar" +
                   "   set hours_start = :hours_start," +
                   "       hours_end = :hours_end," +
                   "       mins_start = :mins_start," +
                   "       mins_end = :mins_end" +
                   " where id = :id")
    @Modifying
    void editDay(@Param("id") Long id,
                 @Param("hours_start") Long hoursStart,
                 @Param("mins_start") Long minsStart,
                 @Param("hours_end") Long hoursEnd,
                 @Param("mins_end") Long minsEnd);

    /**
     * Get user calendar by year.
     * @param year year
     * @param aifUserBotId user bot id
     * @return user calendar
     */
    @Query(value = "select * from aif_user_calendar a where a.year = :year and a.aif_user_bot_id = :aif_user_bot_id")
    List<UserCalendar> findAllByYear(@Param("year") Long year, @Param("aif_user_bot_id") Long aifUserBotId);
}
