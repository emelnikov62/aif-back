package ru.aif.aifback.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.Bot;

/**
 * Bot repository.
 * @author emelnikov
 */
@Repository
public interface BotRepository extends CrudRepository<Bot, Long> {

}
