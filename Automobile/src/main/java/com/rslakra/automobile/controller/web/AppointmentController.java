package com.rslakra.automobile.controller.web;

import com.rslakra.appsuite.core.enums.RoleType;
import com.rslakra.appsuite.spring.controller.web.AbstractWebController;
import com.rslakra.appsuite.spring.filter.Filter;
import com.rslakra.appsuite.spring.parser.Parser;
import com.rslakra.automobile.service.AppointmentService;
import com.rslakra.automobile.service.ScheduleService;
import com.rslakra.automobile.service.ServiceTypeService;
import com.rslakra.automobile.domain.entities.Appointment;
import com.rslakra.automobile.domain.entities.AppointmentStatus;
import com.rslakra.automobile.domain.entities.Schedule;
import com.rslakra.automobile.domain.entities.ServiceType;
import com.rslakra.automobile.service.security.AuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.annotation.security.RolesAllowed;

@Controller
@RequestMapping("/appointments")
public class AppointmentController extends AbstractWebController<Appointment, Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentController.class);

    private final AppointmentService appointmentService;
    private final ServiceTypeService serviceTypeService;
    private final ScheduleService scheduleService;

    /**
     * @param appointmentService
     * @param serviceTypeService
     * @param scheduleService
     */
    @Autowired
    public AppointmentController(AppointmentService appointmentService, ServiceTypeService serviceTypeService,
                                 ScheduleService scheduleService) {
        LOGGER.debug("AppointmentController({}, {}, {})", appointmentService, serviceTypeService, scheduleService);
        this.appointmentService = appointmentService;
        this.serviceTypeService = serviceTypeService;
        this.scheduleService = scheduleService;
    }

    /**
     * Adds service types to the model for dropdowns.
     */
    @ModelAttribute("serviceTypes")
    public java.util.List<ServiceType> getServiceTypes() {
        return serviceTypeService.getAll();
    }

    /**
     * Adds appointment statuses to the model for dropdowns.
     */
    @ModelAttribute("appointmentStatuses")
    public AppointmentStatus[] getAppointmentStatuses() {
        return AppointmentStatus.values();
    }

    /**
     * @return
     */
    @GetMapping("/all")
    @PostFilter("principal.id == filterObject.user.id")
    public List<Appointment> getAppointments() {
        LOGGER.debug("getAppointments()");
        return appointmentService.getAll();
    }


    /**
     * @param auth
     * @return
     */
    @ModelAttribute("isUser")
    public boolean isUser(Authentication auth) {
        LOGGER.debug("isUser({})", auth);
        return (auth != null && auth.getAuthorities().contains(AuthUtils.getAuthority(RoleType.USER)));
    }

    /**
     * Tests the pre-filter annotation.
     *
     * @param auth
     * @return
     */
// @GetMapping("/pre-auth-appointments")
// public String createAppointments(Authentication auth) {
//        AutoUser user = (AutoUser) auth.getPrincipal();
//        // create a new user
//        AutoUser newUser = new AutoUser();
//        newUser.setEmail("work.lakra@gmail.com");
//        newUser.setId(1001L);
//
//        appointmentService.cre
//
//        return utils.saveAll(new ArrayList<Appointment>() {
// {
//                add(Utils.createAppointment(user));
//                add(Utils.createAppointment(newUser));
// }
//        });
// }

    /**
     * @return
     */
    @ModelAttribute
    public Appointment getAppointment() {
        LOGGER.debug("getAppointment()");
        return new Appointment();
    }

    /**
     * Shows form to create a new appointment.
     * Requires a schedule to be selected - redirects to schedules page if not provided.
     *
     * @param scheduleId schedule ID to book (required)
     * @param model
     * @return
     */
    @GetMapping("/new")
    public String newAppointment(@RequestParam(value = "scheduleId", required = false) Long scheduleId, Model model) {
        LOGGER.debug("+newAppointment(scheduleId={}, model={})", scheduleId, model);
        
        // Require schedule selection - redirect to schedules page if not provided
        if (scheduleId == null) {
            LOGGER.debug("-newAppointment(), no scheduleId - redirecting to schedules");
            return "redirect:/schedules";
        }
        
        Schedule schedule = scheduleService.getById(scheduleId);
        
        // Check if schedule is still available
        if (!schedule.hasAvailability()) {
            LOGGER.debug("-newAppointment(), schedule not available - redirecting to schedules");
            return "redirect:/schedules?error=notAvailable";
        }
        
        Appointment appointment = new Appointment();
        appointment.setAppointmentOn(schedule.getScheduleDate());
        model.addAttribute("selectedSchedule", schedule);
        model.addAttribute("appointment", appointment);
        
        LOGGER.debug("-newAppointment(), model: {}", model);
        return "appointment";
    }

    /**
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}")
    // @PostAuthorize("returnObject == 'appointment'")
    @PostAuthorize("hasPermission(#model['appointment'],'read')")
    public String getAppointment(@PathVariable("id") Long id, Model model) {
        LOGGER.debug("+getAppointment({})", id, model);
        Appointment appointment = appointmentService.getById(id);
        model.addAttribute("appointment", appointment);
        LOGGER.debug("-getAppointment(), model: {}", model);
        return "appointment";
    }

    /**
     * @return
     */
    @GetMapping("/confirm")
    @RolesAllowed("ROLE_ADMIN")
    public String confirm() {
        LOGGER.debug("confirm()");
        return "confirmed";
    }

    /**
     * Confirms an appointment (Admin only).
     *
     * @param id
     * @return
     */
    @GetMapping("/confirm/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public String confirmAppointment(@PathVariable("id") Long id) {
        LOGGER.debug("+confirmAppointment({})", id);
        Appointment appointment = appointmentService.getById(id);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointmentService.update(appointment);
        LOGGER.debug("-confirmAppointment()");
        return "redirect:/appointments";
    }

    /**
     * Cancels an appointment.
     *
     * @param id
     * @return
     */
    @GetMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable("id") Long id) {
        LOGGER.debug("+cancelAppointment({})", id);
        Appointment appointment = appointmentService.getById(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentService.update(appointment);
        LOGGER.debug("-cancelAppointment()");
        return "redirect:/appointments";
    }

    /**
     * Completes an appointment (Admin only).
     *
     * @param id
     * @return
     */
    @GetMapping("/complete/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public String completeAppointment(@PathVariable("id") Long id) {
        LOGGER.debug("+completeAppointment({})", id);
        Appointment appointment = appointmentService.getById(id);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentService.update(appointment);
        LOGGER.debug("-completeAppointment()");
        return "redirect:/appointments";
    }

    /**
     * Required by parent interface.
     */
    @Override
    public String save(Appointment appointment) {
        return save(appointment, null, null);
    }

    /**
     * @param appointment
     * @param serviceIds - list of service type IDs from form
     * @param scheduleId - optional schedule ID if booking from schedules page
     * @return
     */
    @PostMapping(value = "/save")
    public String save(@ModelAttribute Appointment appointment,
                       @RequestParam(value = "services", required = false) List<Long> serviceIds,
                       @RequestParam(value = "scheduleId", required = false) Long scheduleId) {
        LOGGER.debug("+save({}, serviceIds={}, scheduleId={})", appointment, serviceIds, scheduleId);
        // Convert service IDs to ServiceType entities
        if (serviceIds != null && !serviceIds.isEmpty()) {
            List<ServiceType> services = new ArrayList<>();
            for (Long id : serviceIds) {
                services.add(serviceTypeService.getById(id));
            }
            appointment.setServices(services);
        }
        // Set time and schedule if scheduleId was provided
        if (scheduleId != null) {
            Schedule schedule = scheduleService.getById(scheduleId);
            appointment.setSchedule(schedule);
            appointment.setStartTime(schedule.getStartTime());
            appointment.setEndTime(schedule.getEndTime());
            // Book the schedule
            scheduleService.bookSchedule(scheduleId);
        }
        
        appointment = appointmentService.create(appointment);
        
        LOGGER.debug("-save(), appointment: {}", appointment);
        return "redirect:/appointments";
    }

    /**
     * Returns appointments for the current user, or all appointments for admin.
     * Sorted by date and time in ascending order.
     *
     * @param model
     * @param auth
     * @return
     */
    @GetMapping({"", "/"})
    @Override
    public String getAll(Model model) {
        LOGGER.debug("+getAll({})", model);
        List<Appointment> appointments = appointmentService.getAppointmentsForCurrentUser();
        model.addAttribute("appointments", appointments);
        LOGGER.debug("-getAll(), appointments count: {}", appointments.size());
        return "appointments";
    }

    /**
     * @param model
     * @param filter
     * @return
     */
    @Override
    public String filter(Model model, Filter filter) {
        LOGGER.debug("filter({}, {})", model, filter);
        return null;
    }

    /**
     * @param model
     * @param idOptional
     * @return
     */
    @Override
    public String editObject(Model model, Optional<Long> idOptional) {
        LOGGER.debug("editObject({}, {})", model, idOptional);
        return null;
    }

    /**
     * @param model
     * @param idOptional
     * @return
     */
    @Override
    public String delete(Model model, Long idOptional) {
        LOGGER.debug("delete({}, {})", model, idOptional);
        return null;
    }

    /**
     * @return
     */
    @Override
    public Parser<Appointment> getParser() {
        return null;
    }

    /**
     * @param model
     * @param allParams
     * @return
     */
    @Override
    public String filter(Model model, Map<String, Object> allParams) {
        return null;
    }
}
