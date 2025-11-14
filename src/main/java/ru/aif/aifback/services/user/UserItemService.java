package ru.aif.aifback.services.user;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.UserItem;
import ru.aif.aifback.model.requests.UserItemRequest;
import ru.aif.aifback.repository.UserItemRepository;

/**
 * User item API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserItemService {

    private final UserItemRepository userItemRepository;

    /**
     * Add user item.
     * @param userItemRequest userItemRequest
     * @return true/false
     */
    public Boolean addItem(UserItemRequest userItemRequest) {
        try {
            Long id = userItemRepository.addUserItem(userItemRequest.getName(),
                                                     userItemRequest.getHours(),
                                                     userItemRequest.getMins(),
                                                     userItemRequest.getAmount(),
                                                     userItemRequest.getId());
            return Objects.isNull(id) ? Boolean.FALSE : Boolean.TRUE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
    }

    /**
     * Find all user items by user bot id.
     * @param id user bot id
     * @return list user items
     */
    public List<UserItem> getUserItems(Long id) {
        return userItemRepository.findAllByUserBotId(id);
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
}
