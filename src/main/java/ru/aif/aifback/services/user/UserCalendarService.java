package ru.aif.aifback.services.user;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.UserCalendarRequest;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.repository.user.UserCalendarRepository;

/**
 * User calendar API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserCalendarService {

    private final UserCalendarRepository userCalendarRepository;

    /**
     * Add days.
     * @param userCalendarRequest user calendar data
     * @return true/false
     */
    public Boolean addDays(UserCalendarRequest userCalendarRequest) {
        try {
            userCalendarRequest.getDays().forEach(day -> {
                userCalendarRepository.addDay(day,
                                              userCalendarRequest.getMonth(),
                                              userCalendarRequest.getYear(),
                                              userCalendarRequest.getHoursStart(),
                                              userCalendarRequest.getMinsStart(),
                                              userCalendarRequest.getHoursEnd(),
                                              userCalendarRequest.getMinsEnd(),
                                              userCalendarRequest.getId(),
                                              userCalendarRequest.getStaffId());
            });
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    /**
     * Get user calendar.
     * @param id id
     * @param month month
     * @param year year
     * @param staffId staff id
     * @return user calendar
     */
    public List<UserCalendar> getUserCalendar(Long id, Long month, Long year, Long staffId) {
        try {
            return userCalendarRepository.getUserCalendar(id, month, year, staffId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Delete days.
     * @param userCalendarRequest user calendar data
     * @return true/false
     */
    public Boolean deleteDays(UserCalendarRequest userCalendarRequest) {
        try {
            userCalendarRepository.deleteDays(userCalendarRequest.getIds(), userCalendarRequest.getId());
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    /**
     * Edit days.
     * @param userCalendarRequest user calendar data
     * @return true/false
     */
    public Boolean editDays(UserCalendarRequest userCalendarRequest) {
        try {
            userCalendarRequest.getIds().forEach(id -> {
                userCalendarRepository.editDay(id,
                                               userCalendarRequest.getHoursStart(),
                                               userCalendarRequest.getMinsStart(),
                                               userCalendarRequest.getHoursEnd(),
                                               userCalendarRequest.getMinsEnd());
            });
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    /**
     * Find all months by year.
     * @param year year
     * @param userBotId user bot id
     * @return user calendar months
     */
    public List<Long> findAllMonthsByYear(Long year, Long userBotId) {
        return userCalendarRepository.findAllMonthsByYear(year, userBotId);
    }

    /**
     * Find all days by year and month.
     * @param year year
     * @param month month
     * @param userBotId user bot id
     * @return user calendar days
     */
    public List<Long> findAllDaysByMonthAndYear(Long year, Long month, Long userBotId) {
        return userCalendarRepository.findAllDaysByMonthAndYear(year, month, userBotId);
    }

    /**
     * Find all times by year and month and day.
     * @param year year
     * @param month month
     * @param day day
     * @param userBotId user bot id
     * @return user calendar times
     */
    public Optional<UserCalendar> findAllDaysByMonthAndYearAndDay(Long year, Long month, Long day, Long userBotId) {
        return userCalendarRepository.findAllDaysByMonthAndYearAndDay(year, month, day, userBotId);
    }

}
