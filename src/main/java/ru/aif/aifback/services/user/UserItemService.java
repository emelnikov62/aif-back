package ru.aif.aifback.services.user;

import static ru.aif.aifback.constants.Constants.MIN_TIME_ITEM;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.UserItemRequest;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.repository.user.UserItemGroupRepository;
import ru.aif.aifback.repository.user.UserItemRepository;

/**
 * User item API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserItemService {

    private final UserItemRepository userItemRepository;
    private final UserItemGroupRepository userItemGroupRepository;

    /**
     * Find user item by id.
     * @param id id
     * @return user item
     */
    public Optional<UserItem> findUserItemById(Long id) {
        return userItemRepository.findUserItemById(id);
    }

    /**
     * Find user item group by item id.
     * @param id id
     * @return user item group
     */
    public Optional<UserItemGroup> findUserItemGroupByItemId(Long id) {
        return userItemGroupRepository.findById(id);
    }

    /**
     * Add user item.
     * @param userItemRequest userItemRequest
     * @return true/false
     */
    public Boolean addItem(UserItemRequest userItemRequest) {
        try {
            byte[] fileData = null;
            if (Objects.nonNull(userItemRequest.getFile())) {
                fileData = Base64.getEncoder().encode(userItemRequest.getFile().getBytes());
            }

            Long id = userItemRepository.addUserItem(userItemRequest.getName(),
                                                     userItemRequest.getHours(),
                                                     userItemRequest.getMins(),
                                                     userItemRequest.getAmount(),
                                                     userItemRequest.getId(),
                                                     fileData);
            return Objects.isNull(id) ? Boolean.FALSE : Boolean.TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    /**
     * Find all user items by group id.
     * @param groupId group id
     * @return list user items
     */
    public List<UserItem> getUserItemsByGroupId(Long groupId) {
        return userItemRepository.findAllByGroupId(groupId);
    }

    /**
     * Find all user item groups by user bot id.
     * @param id user bot id
     * @return list user item groups
     */
    public List<UserItemGroup> getUserItemGroups(Long id) {
        List<UserItemGroup> groups = userItemGroupRepository.findAllByBotId(id);
        groups.forEach(group -> {
            group.setItems(getUserItemsByGroupId(group.getId()));
        });
        return groups;
    }

    /**
     * Delete user item.
     * @param id id
     * @return true/false
     */
    public Boolean deleteUserItem(Long id) {
        try {
            userItemRepository.deleteUserItem(id);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    /**
     * Delete user group item.
     * @param id id
     * @return true/false
     */
    public Boolean deleteUserItemGroup(Long id) {
        try {
            userItemRepository.deleteUserItemsByGroupId(id);
            userItemGroupRepository.deleteUserItemGroup(id);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    /**
     * Update user item active.
     * @param id id
     * @param active active
     * @return true/false
     */
    public Boolean updateUserItemActive(Long id, boolean active) {
        try {
            userItemRepository.updateUserItemActive(active, id);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    /**
     * Update user item group active.
     * @param id id
     * @param active active
     * @return true/false
     */
    public Boolean updateUserItemGroupActive(Long id, boolean active) {
        try {
            userItemGroupRepository.updateUserItemGroupActive(active, id);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    /**
     * Add user group item.
     * @param userItemRequest user group item data
     * @return true/false
     */
    public Boolean addUserGroupItem(UserItemRequest userItemRequest) {
        try {
            UserItemGroup userItemGroup = new UserItemGroup(userItemRequest.getId(), userItemRequest.getName(), Boolean.FALSE, LocalDateTime.now());
            userItemGroupRepository.save(userItemGroup);

            return Objects.isNull(userItemGroup.getId()) ? Boolean.FALSE : Boolean.TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    /**
     * Get min time user item.
     * @param userBotId user bot id
     * @return min time user item
     */
    public Long getMinTimeUserItem(Long userBotId) {
        return userItemRepository.findMinimumItemTime(userBotId).orElse(MIN_TIME_ITEM);
    }

    /**
     * Find all user items by user staff.
     * @param aifUserStaffId user staff id
     * @param aifUserBotId user bot id
     * @return user items
     */
    public List<UserItem> findAllByUserStaff(Long aifUserStaffId, Long aifUserBotId) {
        return userItemRepository.findAllByUserStaff(aifUserBotId, aifUserStaffId);
    }
}
