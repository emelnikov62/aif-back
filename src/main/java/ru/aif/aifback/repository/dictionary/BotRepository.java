package ru.aif.aifback.repository.dictionary;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.dictionary.Bot;

/**
 * Bot repository.
 * @author emelnikov
 */
@Repository
public interface BotRepository extends CrudRepository<Bot, Long> {

}
