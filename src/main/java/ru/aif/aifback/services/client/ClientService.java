package ru.aif.aifback.services.client;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.Client;
import ru.aif.aifback.repository.client.ClientRepository;

/**
 * Client API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    /**
     * Get client by tg id.
     * @param tgId tg id
     * @return client data
     */
    public Optional<Client> getClientByTgId(String tgId) {
        return clientRepository.findByTgId(tgId);
    }

    /**
     * Create client.
     * @param tgId tg id
     * @return client data
     */
    public Optional<Long> createClient(String tgId) {
        try {
            return Optional.of(clientRepository.addClient(tgId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
