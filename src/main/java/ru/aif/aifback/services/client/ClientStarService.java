package ru.aif.aifback.services.client;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientStar;
import ru.aif.aifback.repository.client.ClientStarRepository;

/**
 * Client star API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class ClientStarService {

    private final ClientStarRepository clientStarRepository;

    /**
     * Find client stat by user item and staff.
     * @param clientId client id
     * @param userItemId user item id
     * @param staffId staff id
     * @return client star
     */
    public ClientStar findClientStarByUserItemAndStaff(Long clientId, Long userItemId, Long staffId) {
        try {
            return clientStarRepository.findByClientAndUserItemAndStaff(clientId, userItemId, staffId).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Add client star
     * @param clientId client id
     * @param userBotId user bot id
     * @param userItemId user item id
     * @param staffId staff id
     * @param value value
     * @return added id
     */
    public Long addClientStar(Long clientId, Long userBotId, Long userItemId, Long staffId, Long value) {
        try {
            ClientStar clientStar = new ClientStar(clientId, userBotId, userItemId, staffId, value, LocalDateTime.now());
            clientStarRepository.save(clientStar);

            return clientStar.getId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Calc star by staff and user item.
     * @param userBotId user bot id
     * @param staffId staff id
     * @param userItemId user item id
     * @return calc star
     */
    public Float calcByStaffAndUserItem(Long userBotId, Long staffId, Long userItemId) {
        List<Integer> stars = clientStarRepository.calcByStaffAndUserItem(userBotId, staffId, userItemId);
        return stars.isEmpty()
               ? 5f
               : Math.round((float) stars.stream().mapToInt(Integer::intValue).sum() / stars.size());
    }

    /**
     * Calc star by user item.
     * @param userBotId user bot id
     * @param userItemId user item id
     * @return calc star
     */
    public Float calcByUserItem(Long userBotId, Long userItemId) {
        List<Integer> stars = clientStarRepository.calcByUserItem(userBotId, userItemId);
        return stars.isEmpty()
               ? 5f
               : Math.round((float) stars.stream().mapToInt(Integer::intValue).sum() / stars.size());
    }

}
