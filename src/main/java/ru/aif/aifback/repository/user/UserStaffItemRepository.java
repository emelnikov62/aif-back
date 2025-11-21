package ru.aif.aifback.repository.user;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.user.UserStaffItem;

/**
 * User staff item repository.
 * @author emelnikov
 */
@Repository
public interface UserStaffItemRepository extends CrudRepository<UserStaffItem, Long> {

    /**
     * Find all by user staff id.
     * @param userStaffId user staff id
     * @return user staff items
     */
    @Query(value = "select a.* from aif_user_staff_items a where a.aif_user_staff_id = :aif_user_staff_id")
    List<UserStaffItem> findAllByUserStaffId(@Param("aif_user_staff_id") Long userStaffId);

    /**
     * Update user staff item active.
     * @param active active
     * @param id id
     */
    @Query(value = "update aif_user_staff_items set active = :active where id = :id")
    @Modifying
    void updateUserStaffItemActive(@Param("active") boolean active, @Param("id") Long id);

    @Query(value = "insert into aif_user_staff_items(aif_user_staff_id, aif_user_item_id) values (:aif_user_staff_id, :aif_user_item_id)")
    @Modifying
    void addLinkToItem(@Param("aif_user_staff_id") Long aifUserStaffId, @Param("aif_user_item_id") Long aifUserItemId);

}
