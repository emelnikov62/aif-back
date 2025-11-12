package ru.aif.aifback.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.constants.Constants;

/**
 * Admin controller.
 * @author emelnikov
 */
@RestController
@Slf4j
@RequestMapping(Constants.MAIN_URL + Constants.ADMIN_URL)
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class AdminController {

    @GetMapping(value = "/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }
}
