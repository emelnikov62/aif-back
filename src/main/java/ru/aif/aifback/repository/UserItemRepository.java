package ru.aif.aifback.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.UserItem;

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
     * @param aifUserBotId user bot id
     * @return id
     */
    @Query(value = "insert into aif_user_items(name, hours, mins, amount, aif_user_bot_id) " +
                   "values(:name, :hours, :mins, :amount, :aif_user_bot_id)")
    @Modifying
    Long addUserItem(@Param("name") String name,
                     @Param("hours") Long hours,
                     @Param("mins") Long mins,
                     @Param("amount") BigDecimal amount,
                     @Param("aif_user_bot_id") Long aifUserBotId);

    /**
     * Find all user items by user bot id.
     * @param aifUserBotId user bot id
     * @return list user items
     */
    @Query(value = "select * from aif_user_items a where a.aif_user_bot_id = :aif_user_bot_id")
    List<UserItem> findAllByUserBotId(@Param("aif_user_bot_id") Long aifUserBotId);

    /**
     * Delete user item.
     * @param id id
     */
    @Query(value = "delete from aif_user_items where id = :id")
    @Modifying
    void deleteUserItem(@Param("id") Long id);
}
