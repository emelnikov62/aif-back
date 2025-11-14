package ru.aif.aifback.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.constants.Constants;
import ru.aif.aifback.model.UserItem;
import ru.aif.aifback.model.requests.UserItemRequest;
import ru.aif.aifback.services.user.UserItemService;

/**
 * UI controller.
 * @author emelnikov
 */
@Slf4j
@Controller
@RequestMapping(Constants.MAIN_URL + Constants.ADMIN_URL)
@RequiredArgsConstructor
public class UiController {

    private final UserItemService userItemService;

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

    /**
     * Admin items bot form.
     * @param id id
     * @param model model
     * @return true/false
     */
    @GetMapping(value = "/items-bot-form")
    public String itemsForm(@RequestParam(name = "id") String id, Model model) {
        model.addAttribute("id", id);
        return "items_bot_form";
    }

    /**
     * Add user item.
     * @param userItemRequest userItemRequest
     * @return true/false
     */
    @PostMapping(value = "/add-user-item", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> addItem(@RequestBody UserItemRequest userItemRequest) {
        return ResponseEntity.ok(userItemService.addItem(userItemRequest));
    }

    /**
     * Get user items.
     * @param userBotId user bot id
     * @return list user items
     */
    @GetMapping(value = "/list-user-items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserItem>> getUserItems(@RequestParam(name = "id") Long userBotId) {
        return ResponseEntity.ok(userItemService.getUserItems(userBotId));
    }

    /**
     * Delete user item.
     * @param id user item id
     * @return true/false
     */
    @GetMapping(value = "/user-item-delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteUserItem(@RequestParam(name = "id") Long id) {
        return ResponseEntity.ok(userItemService.deleteUserItem(id));
    }
}