package ru.aif.aifback.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * TG scheduler properties.
 * @author emelnikov
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "scheduler.telegram")
public class TgSchedulerProperties {

    private Boolean enabled;
    private String completedServices;
}
