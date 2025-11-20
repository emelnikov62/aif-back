package ru.aif.aifback.controller;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.constants.Constants;
import ru.aif.aifback.model.UserCalendar;
import ru.aif.aifback.model.UserItemGroup;
import ru.aif.aifback.model.requests.UserCalendarRequest;
import ru.aif.aifback.model.requests.UserItemRequest;
import ru.aif.aifback.services.user.UserCalendarService;
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
    private final UserCalendarService userCalendarService;

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
     * Admin calendar bot form.
     * @param id id
     * @param model model
     * @return true/false
     */
    @GetMapping(value = "/calendar-bot-form")
    public String calendarBotForm(@RequestParam(name = "id") String id, Model model) {
        model.addAttribute("id", id);
        return "calendar_bot_form";
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
     * @param file file
     * @param name name
     * @param id id
     * @param hours hours
     * @param mins mins
     * @param amount amount
     * @return true/false
     */
    @PostMapping(value = "/add-user-item", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Boolean> addItem(@RequestPart(value = "service_file", required = false) MultipartFile file,
                                           @RequestParam("name") String name,
                                           @RequestParam("id") Long id,
                                           @RequestParam("hours") Long hours,
                                           @RequestParam("mins") Long mins,
                                           @RequestParam("amount") BigDecimal amount) {
        return ResponseEntity.ok(userItemService.addItem(UserItemRequest.builder()
                                                                        .id(id)
                                                                        .name(name)
                                                                        .hours(hours)
                                                                        .mins(mins)
                                                                        .amount(amount)
                                                                        .file(file)
                                                                        .build()));
    }

    /**
     * Get user items.
     * @param userBotId user bot id
     * @return list user items
     */
    @GetMapping(value = "/list-user-item-groups", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserItemGroup>> getUserItemGroups(@RequestParam(name = "id") Long userBotId) {
        return ResponseEntity.ok(userItemService.getUserItemGroups(userBotId));
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

    /**
     * Delete user group item.
     * @param id user group item id
     * @return true/false
     */
    @GetMapping(value = "/user-item-group-delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteUserItemGroup(@RequestParam(name = "id") Long id) {
        return ResponseEntity.ok(userItemService.deleteUserItemGroup(id));
    }

    /**
     * Update user item active.
     * @param userItemRequest userItemRequest
     * @return true/false
     */
    @PostMapping(value = "/update-user-item-active", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateUserItemActive(@RequestBody UserItemRequest userItemRequest) {
        return ResponseEntity.ok(userItemService.updateUserItemActive(userItemRequest.getId(), userItemRequest.getActive()));
    }

    /**
     * Update user item group active.
     * @param userItemRequest userItemRequest
     * @return true/false
     */
    @PostMapping(value = "/update-user-item-group-active", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> updateUserItemGroupActive(@RequestBody UserItemRequest userItemRequest) {
        return ResponseEntity.ok(userItemService.updateUserItemGroupActive(userItemRequest.getId(), userItemRequest.getActive()));
    }

    /**
     * Add user group item.
     * @param userItemRequest userItemRequest
     * @return true/false
     */
    @PostMapping(value = "/add-user-group-item", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> addUserGroupItem(@RequestBody UserItemRequest userItemRequest) {
        return ResponseEntity.ok(userItemService.addUserGroupItem(userItemRequest));
    }

    /**
     * Add user calendar.
     * @param userCalendarRequest userCalendarRequest
     * @return true/false
     */
    @PostMapping(value = "/add-user-calendar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> addUserCalendar(@RequestBody UserCalendarRequest userCalendarRequest) {
        return ResponseEntity.ok(userCalendarService.addDays(userCalendarRequest));
    }

    /**
     * Get user calendar.
     * @param id user bot id
     * @param month month
     * @param year year
     * @return user calendar
     */
    @GetMapping(value = "/user-calendar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserCalendar>> getUserCalendar(@RequestParam(name = "id") Long id,
                                                              @RequestParam(name = "month") Long month,
                                                              @RequestParam(name = "year") Long year) {
        return ResponseEntity.ok(userCalendarService.getUserCalendar(id, month, year));
    }

    /**
     * Delete user calendar.
     * @param userCalendarRequest userCalendarRequest
     * @return true/false
     */
    @PostMapping(value = "/delete-user-calendar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteUserCalendar(@RequestBody UserCalendarRequest userCalendarRequest) {
        return ResponseEntity.ok(userCalendarService.deleteDays(userCalendarRequest));
    }

    /**
     * Edit user calendar.
     * @param userCalendarRequest userCalendarRequest
     * @return true/false
     */
    @PostMapping(value = "/edit-user-calendar", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> editUserCalendar(@RequestBody UserCalendarRequest userCalendarRequest) {
        return ResponseEntity.ok(userCalendarService.editDays(userCalendarRequest));
    }
}