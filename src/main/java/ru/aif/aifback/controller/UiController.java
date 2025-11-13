package ru.aif.aifback.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.constants.Constants;

/**
 * UI controller.
 * @author emelnikov
 */
@Slf4j
@Controller
@RequestMapping(Constants.MAIN_URL + Constants.ADMIN_URL)
public class UiController {

    /**
     * Admin link bot form.
     * @param id id
     * @param model model
     * @return true/false
     */
    @GetMapping(value = "/link-bot-form")
    public String linkBotForm(@RequestParam(name = "id") String id, Model model) {
        model.addAttribute("id", id);
        return "link_bot_form";
    }
}