package ru.aif.aifback.repository.client;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.client.ClientRecord;

/**
 * Client record repository.
 * @author emelnikov
 */
@Repository
public interface ClientRecordRepository extends CrudRepository<ClientRecord, Long> {

    /**
     * Get client record by id.
     * @param id id
     * @return client record data
     */
    Optional<ClientRecord> findById(Long id);

}
