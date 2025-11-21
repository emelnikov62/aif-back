package ru.aif.aifback.repository.user;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.user.UserItemGroup;

/**
 * User group item repository.
 * @author emelnikov
 */
@Repository
public interface UserItemGroupRepository extends CrudRepository<UserItemGroup, Long> {

    /**
     * Find all groups by bot id.
     * @param aifUserBotId bot id
     * @return list groups
     */
    @Query(value = "select * from aif_user_item_groups a where a.aif_user_bot_id = :aif_user_bot_id")
    List<UserItemGroup> findAllByBotId(@Param("aif_user_bot_id") Long aifUserBotId);

    /**
     * Find all groups by bot id.
     * @param aifUserBotId bot id
     * @return list groups
     */
    @Query(value = "select a.*" +
                   "  from aif_user_item_groups a" +
                   " where a.aif_user_bot_id = :aif_user_bot_id" +
                   "   and a.active")
    List<UserItemGroup> findAllByBotIdAndActive(@Param("aif_user_bot_id") Long aifUserBotId);

    /**
     * Delete user group item.
     * @param id id
     */
    @Query(value = "delete from aif_user_item_groups where id = :id")
    @Modifying
    void deleteUserItemGroup(@Param("id") Long id);

    /**
     * Update user item group active.
     * @param active active
     * @param id id
     */
    @Query(value = "update aif_user_item_groups set active = :active where id = :id")
    @Modifying
    void updateUserItemGroupActive(@Param("active") boolean active, @Param("id") Long id);

}
