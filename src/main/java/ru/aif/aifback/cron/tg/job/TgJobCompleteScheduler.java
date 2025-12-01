package ru.aif.aifback.cron.tg.job;

import static ru.aif.aifback.services.tg.enums.TgClientRecordType.ACTIVE;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.FINISHED;

import java.util.List;
import java.util.Objects;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.cron.tg.TgScheduler;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.tg.admin.TgAdminNotificationService;
import ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordNotificationService;

/**
 * TG service for complete services by date.
 * @author emelnikov
 */
@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(prefix = "scheduler.telegram", name = "enabled", havingValue = "true")
public class TgJobCompleteScheduler implements TgScheduler {

    private final ClientRecordService clientRecordService;
    private final TgAdminNotificationService tgAdminNotificationService;
    private final TgClientBotRecordNotificationService tgClientBotRecordNotificationService;

    /**
     * Completed services by date.
     */
    @Override
    @Scheduled(cron = "${scheduler.telegram.completed-services}")
    public void process() {
        List<ClientRecord> records = clientRecordService.findAllForCompleted(ACTIVE.getType());
        if (records.isEmpty()) {
            return;
        }

        records.forEach(record -> {
            if (clientRecordService.completeService(record.getId())) {
                log.info("Complete service: {}", record.getId());

                if (Objects.nonNull(record.getUserBot()) && Objects.nonNull(record.getUserBot().getUser()) && Objects.nonNull(record.getClient())) {
                    tgAdminNotificationService.recordCompleteNotification(record);
                    tgClientBotRecordNotificationService.recordNotification(record, FINISHED);
                }
            } else {
                log.info("Error complete service: {}", record.getId());
            }
        });
    }
}
