package ru.aif.aifback.services.user;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.UserCalendar;
import ru.aif.aifback.model.requests.UserCalendarRequest;
import ru.aif.aifback.repository.UserCalendarRepository;

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
                                              userCalendarRequest.getId());
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
     * @return user calendar
     */
    public List<UserCalendar> getUserCalendar(Long id, Long month, Long year) {
        try {
            return userCalendarRepository.getUserCalendar(id, month, year);
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

}
