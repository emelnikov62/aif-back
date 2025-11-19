package ru.aif.aifback.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.UserCalendar;

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

    @Query(value = "select * from aif_user_calendar a where a.aif_user_bot_id = :aif_user_bot_id and a.month = :month and a.year = :year")
    List<UserCalendar> getUserCalendar(@Param("aif_user_bot_id") Long aifUserBotId,
                                       @Param("month") Long month,
                                       @Param("year") Long year);

}
