package ru.aif.aifback.services.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.UserItemRequest;
import ru.aif.aifback.model.user.UserStaff;
import ru.aif.aifback.repository.user.UserStaffRepository;

/**
 * User staff API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserStaffService {

    private final UserStaffRepository userStaffRepository;
    private final UserItemService userItemService;

    /**
     * Get user staffs.
     * @param userBotId user bot id
     * @return user staffs
     */
    public List<UserStaff> getUserStaffs(Long userBotId) {
        List<UserStaff> userStaffs = userStaffRepository.findAllByUserBotId(userBotId);
        userStaffs.forEach(userStaff -> {
            userStaff.setItems(userItemService.findAllByUserStaff(userStaff.getId(), userBotId));
        });

        return userStaffs;
    }

    /**
     * Add user staff.
     * @param userItemRequest userItemRequest
     * @return true/false
     */
    public Boolean addUserStaff(UserItemRequest userItemRequest) {
        try {
            UserStaff userStaff = new UserStaff(userItemRequest.getId(),
                                                userItemRequest.getName(),
                                                userItemRequest.getSurname(),
                                                userItemRequest.getThird(),
                                                Boolean.FALSE,
                                                LocalDateTime.now());
            userStaffRepository.save(userStaff);

            if (Objects.isNull(userStaff.getId())) {
                return Boolean.FALSE;
            }

            userItemRequest.getServices().forEach(service -> userStaffRepository.addLinkToItem(userStaff.getId(), service));

            return Boolean.TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    /**
     * Update user staff active.
     * @param id id
     * @param active active
     * @return true/false
     */
    public Boolean updateUserStaffActive(Long id, boolean active) {
        try {
            userStaffRepository.updateUserStaffActive(active, id);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    /**
     * Update user staff item active.
     * @param id id
     * @param active active
     * @return true/false
     */
    public Boolean updateUserStaffItemActive(Long id, boolean active) {
        try {
            userStaffRepository.updateUserStaffItemActive(active, id);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

}
