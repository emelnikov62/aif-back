package ru.aif.aifback.repository.user;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.user.UserItem;

/**
 * User item repository.
 * @author emelnikov
 */
@Repository
public interface UserItemRepository extends CrudRepository<UserItem, Long> {

    /**
     * Add user item.
     * @param name name
     * @param hours hours
     * @param mins mins
     * @param amount amount
     * @param aifUserItemGroupId user item group id
     * @param fileData file data
     * @return id
     */
    @Query(value = "insert into aif_user_items(name, hours, mins, amount, aif_user_item_group_id, file_data) " +
                   "values(:name, :hours, :mins, :amount, :aif_user_item_group_id, :file_data)")
    @Modifying
    Long addUserItem(@Param("name") String name,
                     @Param("hours") Long hours,
                     @Param("mins") Long mins,
                     @Param("amount") BigDecimal amount,
                     @Param("aif_user_item_group_id") Long aifUserItemGroupId,
                     @Param("file_data") byte[] fileData);

    /**
     * Find all user items by group id.
     * @param groupId group id
     * @return list user items
     */
    @Query(value = "select a.id, " +
                   "       a.name," +
                   "       a.hours," +
                   "       a.mins," +
                   "       a.amount," +
                   "       a.active," +
                   "       convert_from(a.file_data, 'UTF-8') as file_data," +
                   "       a.created," +
                   "       a.aif_user_item_group_id" +
                   "  from aif_user_items a" +
                   " where a.aif_user_item_group_id = :group_id")
    List<UserItem> findAllByGroupId(@Param("group_id") Long groupId);

    /**
     * Find all user items by group id and active.
     * @param groupId group id
     * @return list user items
     */
    @Query(value = "select a.id, " +
                   "       a.name," +
                   "       a.hours," +
                   "       a.mins," +
                   "       a.amount," +
                   "       a.active," +
                   "       convert_from(a.file_data, 'UTF-8') as file_data," +
                   "       a.created," +
                   "       a.aif_user_item_group_id" +
                   "  from aif_user_items a" +
                   " where a.aif_user_item_group_id = :group_id and a.active")
    List<UserItem> findAllByGroupIdAndActive(@Param("group_id") Long groupId);

    /**
     * Find all user items by group id and staff id.
     * @param groupId group id
     * @param staffId staff id
     * @return list user items
     */
    @Query(value = "select a.id, " +
                   "       a.name," +
                   "       a.hours," +
                   "       a.mins," +
                   "       a.amount," +
                   "       a.active," +
                   "       convert_from(a.file_data, 'UTF-8') as file_data," +
                   "       a.created," +
                   "       a.aif_user_item_group_id" +
                   "  from aif_user_items a" +
                   " where a.aif_user_item_group_id = :group_id" +
                   "   and not exists(" +
                   "        select null" +
                   "          from aif_user_staff_items asi" +
                   "         where asi.aif_user_item_id = a.id" +
                   "           and asi.aif_user_staff_id = :staff_id" +
                   "   )")
    List<UserItem> findAllByGroupIdAndStaffId(@Param("group_id") Long groupId, @Param("staff_id") Long staffId);

    /**
     * Delete user item.
     * @param id id
     */
    @Query(value = "delete from aif_user_items where id = :id")
    @Modifying
    void deleteUserItem(@Param("id") Long id);

    /**
     * Delete user items by group.
     * @param id group id
     */
    @Query(value = "delete from aif_user_items where aif_user_item_group_id = :group_id")
    @Modifying
    void deleteUserItemsByGroupId(@Param("group_id") Long id);

    /**
     * Update user item active.
     * @param active active
     * @param id id
     */
    @Query(value = "update aif_user_items set active = :active where id = :id")
    @Modifying
    void updateUserItemActive(@Param("active") boolean active, @Param("id") Long id);

    @Query(value = "select a.id, " +
                   "       a.name," +
                   "       a.hours," +
                   "       a.mins," +
                   "       a.amount," +
                   "       a.active," +
                   "       convert_from(a.file_data, 'UTF-8') as file_data," +
                   "       a.created," +
                   "       a.aif_user_item_group_id" +
                   "  from aif_user_items a" +
                   " where a.id = :id")
    Optional<UserItem> findUserItemById(@Param("id") Long id);

    /**
     * Find min user item.
     * @param aifUserBotId user bot id
     */
    @Query(value = "select times.id, " +
                   "       times.name," +
                   "       times.hours," +
                   "       times.mins," +
                   "       times.amount," +
                   "       times.active," +
                   "       convert_from(times.file_data, 'UTF-8') as file_data," +
                   "       times.created," +
                   "       times.aif_user_item_group_id" +
                   "  from (" +
                   "    select a.hours * 60 + a.mins as time, a.* from aif_user_items a" +
                   "      join aif_user_item_groups g on a.aif_user_item_group_id = g.id" +
                   "     where g.aif_user_bot_id = :aif_user_bot_id" +
                   "      ) times" +
                   "  order by times.time" +
                   "  limit 1")
    Optional<UserItem> findMinimumUserItem(@Param("aif_user_bot_id") Long aifUserBotId);

    /**
     * Find user item by user staff item id.
     * @param aifUserStaffItemId user staff item id
     * @return user item
     */
    @Query(value = "select a.id, " +
                   "       a.name," +
                   "       a.hours," +
                   "       a.mins," +
                   "       a.amount," +
                   "       a.active," +
                   "       convert_from(a.file_data, 'UTF-8') as file_data," +
                   "       a.created," +
                   "       a.aif_user_item_group_id" +
                   "  from aif_user_items a" +
                   "  join aif_user_staff_items asi on asi.aif_user_item_id = a.id" +
                   " where asi.id = :aif_user_bot_id")
    UserItem findByUserStaffItemId(@Param("aif_user_bot_id") Long aifUserStaffItemId);
}
