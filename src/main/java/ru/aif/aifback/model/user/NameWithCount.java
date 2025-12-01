package ru.aif.aifback.model.user;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Model name with count.
 * @author emelnikov
 */
@Slf4j
@Data
public class NameWithCount {

    private String name;
    private Long count;
}
