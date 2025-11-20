package ru.aif.aifback.services.client;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.repository.client.ClientRecordRepository;

/**
 * Client record API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class ClientRecordService {

    private final ClientRecordRepository clientRecordRepository;

    /**
     * Get client record by id.
     * @param id id
     * @return client record data
     */
    public Optional<ClientRecord> getClientRecordById(Long id) {
        return clientRecordRepository.findById(id);
    }

    /**
     * Create client record
     * @param clientId client id
     * @param userBotId user bot id
     * @param userItemId user item id
     * @param userCalendarId user calendar id
     * @param hours hours
     * @param mins mins
     * @return id
     */
    public Optional<Long> addClientRecord(Long clientId, Long userBotId, Long userItemId, Long userCalendarId, Long hours, Long mins) {
        try {
            return Optional.of(clientRecordRepository.addClientRecord(clientId, userBotId, userItemId, userCalendarId, hours, mins));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
