package ru.aif.aifback.repository.user;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.user.UserBot;

/**
 * User bot repository.
 * @author emelnikov
 */
@Repository
public interface UserBotRepository extends CrudRepository<UserBot, Long> {

    /**
     * List user bots by tg id;
     * @param id tg id
     * @return list user bots
     */
    @Query(value = "select * from aif_user_bots b where b.aif_user_id = :id")
    List<UserBot> findAllByAifUserId(Long id);

    /**
     * Set token bot.
     * @param id id
     * @param token token
     */
    @Query(value = "update aif_user_bots set token = :token where id = :id")
    @Modifying
    void linkBot(@Param("id") Long id, @Param("token") String token);
}
