package ru.aif.aifback.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.UserBot;

/**
 * User bot repository.
 * @author emelnikov
 */
@Repository
public interface UserBotRepository extends CrudRepository<UserBot, Long> {

}
