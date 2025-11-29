package com.rslakra.automobile.controller.web;

import com.rslakra.automobile.domain.entities.Schedule;
import com.rslakra.automobile.service.ScheduleService;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for managing schedules.
 *
 * @author Rohtash Lakra
 */
@Controller
@RequestMapping("/schedules")
public class ScheduleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleController.class);

    private final ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * Check if user is admin.
     *
     * @param auth
     * @return
     */
    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication auth) {
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Display available schedules page.
     * Admin sees all schedules; users see only available schedules.
     *
     * @param model
     * @param auth
     * @return
     */
    @GetMapping({"", "/"})
    public String schedulesPage(Model model, Authentication auth) {
        LOGGER.debug("+schedulesPage()");
        List<Schedule> schedules;
        
        if (isAdmin(auth)) {
            schedules = scheduleService.getAllSchedules();
        } else {
            schedules = scheduleService.getAvailableSchedules();
        }
        
        model.addAttribute("schedules", schedules);
        model.addAttribute("schedule", new Schedule());
        LOGGER.debug("-schedulesPage(), schedules: {}", schedules.size());
        return "schedules";
    }

    /**
     * Admin: Create a new schedule.
     *
     * @param schedule
     * @return
     */
    @PostMapping("/save")
    @RolesAllowed("ROLE_ADMIN")
    public String saveSchedule(@ModelAttribute Schedule schedule) {
        LOGGER.debug("+saveSchedule({})", schedule);
        scheduleService.create(schedule);
        LOGGER.debug("-saveSchedule()");
        return "redirect:/schedules";
    }

    /**
     * Admin: Generate default schedules for a date range.
     *
     * @param startDate
     * @param endDate
     * @return
     */
    @PostMapping("/generate")
    @RolesAllowed("ROLE_ADMIN")
    public String generateSchedules(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        LOGGER.debug("+generateSchedules({}, {})", startDate, endDate);
        scheduleService.generateDefaultSchedules(startDate, endDate);
        LOGGER.debug("-generateSchedules()");
        return "redirect:/schedules";
    }

    /**
     * Admin: Delete a schedule.
     *
     * @param id
     * @return
     */
    @GetMapping("/delete/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public String deleteSchedule(@PathVariable("id") Long id) {
        LOGGER.debug("+deleteSchedule({})", id);
        scheduleService.delete(id);
        LOGGER.debug("-deleteSchedule()");
        return "redirect:/schedules";
    }

    /**
     * Admin: Toggle availability of a schedule.
     *
     * @param id
     * @return
     */
    @GetMapping("/toggle/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public String toggleAvailability(@PathVariable("id") Long id) {
        LOGGER.debug("+toggleAvailability({})", id);
        Schedule schedule = scheduleService.getById(id);
        schedule.setAvailable(!schedule.isAvailable());
        scheduleService.update(schedule);
        LOGGER.debug("-toggleAvailability()");
        return "redirect:/schedules";
    }
}
