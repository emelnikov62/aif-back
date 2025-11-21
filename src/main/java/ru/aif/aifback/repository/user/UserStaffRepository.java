package ru.aif.aifback.repository.user;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.user.UserStaff;

/**
 * User staff repository.
 * @author emelnikov
 */
@Repository
public interface UserStaffRepository extends CrudRepository<UserStaff, Long> {

    /**
     * Find all by user bot id.
     * @param userBotId user bot id
     * @return user staffs
     */
    @Query(value = "select a.* from aif_user_staffs a where a.aif_user_bot_id = :aif_user_bot_id")
    List<UserStaff> findAllByUserBotId(@Param("aif_user_bot_id") Long userBotId);

    /**
     * Update user staff active.
     * @param active active
     * @param id id
     */
    @Query(value = "update aif_user_staffs set active = :active where id = :id")
    @Modifying
    void updateUserStaffActive(@Param("active") boolean active, @Param("id") Long id);

}
