package ru.aif.aifback.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.UserBot;

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
}
