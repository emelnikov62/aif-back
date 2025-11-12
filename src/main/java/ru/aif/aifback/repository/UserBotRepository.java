package ru.aif.aifback.repository;

import java.util.Iterator;

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
    Iterator<UserBot> findAllByTgId(Long id);
}
