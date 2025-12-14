package com.rslakra.automobile.service.impl;

import com.rslakra.appsuite.core.BeanUtils;
import com.rslakra.appsuite.spring.exception.AuthenticationException;
import com.rslakra.appsuite.spring.exception.NoRecordFoundException;
import com.rslakra.appsuite.spring.filter.Filter;
import com.rslakra.appsuite.spring.persistence.ServiceOperation;
import com.rslakra.appsuite.spring.service.AbstractServiceImpl;
import com.rslakra.automobile.domain.entities.Appointment;
import com.rslakra.automobile.domain.entities.AppointmentStatus;
import com.rslakra.automobile.domain.entities.AutoUser;
import com.rslakra.automobile.domain.repositories.AppointmentRepository;
import com.rslakra.automobile.service.AppointmentService;
import com.rslakra.automobile.service.security.context.ContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Rohtash Lakra
 * @created 4/20/23 5:59 PM
 */
@Service
public class AppointmentServiceImpl extends AbstractServiceImpl<Appointment, Long> implements AppointmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentRepository appointmentRepository;

    /**
     * @param appointmentRepository
     */
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
        LOGGER.debug("AppointmentServiceImpl({})", appointmentRepository);
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * @param operation
     * @param appointment
     * @return
     */
    @Override
    public Appointment validate(ServiceOperation operation, Appointment appointment) {
        return appointment;
    }

    /**
     * @param appointment
     * @return
     */
    @Override
    public Appointment create(Appointment appointment) {
        AutoUser autoUser = (AutoUser) ContextUtils.INSTANCE.getAuthentication().getPrincipal();
        appointment.setUser(autoUser);
        if (appointment.getStatus() == null) {
            appointment.setStatus(AppointmentStatus.PENDING);
        }
        appointment = appointmentRepository.save(appointment);
        return appointment;
    }

    /**
     * @param appointments
     * @return
     */
    @Override
    public List<Appointment> create(List<Appointment> appointments) {
        final List<Appointment> appointmentList = new ArrayList<>();
        appointments.forEach(appointment -> appointmentList.add(create(appointment)));
        return appointmentList;
    }

    /**
     * @return
     */
    @Override
    public List<Appointment> getAll() {
        LOGGER.debug("+getAll()");
        AutoUser autoUser = ContextUtils.getLoggedInUser();
        if (BeanUtils.isNull(autoUser)) {
            throw new AuthenticationException();
        }

        List<Appointment> appointments;
        try {
            appointments = appointmentRepository.findAll();
        } catch (Exception ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
            appointments = new ArrayList<>();
        }

        LOGGER.debug("+getAll(), appointments: {}", appointments);
        return appointments;
    }

    /**
     * Returns appointments for the current user.
     * Admin users see all appointments sorted by date/time.
     * Regular users see only their own appointments sorted by date/time.
     *
     * @return
     */
    @Override
    public List<Appointment> getAppointmentsForCurrentUser() {
        LOGGER.debug("+getAppointmentsForCurrentUser()");
        AutoUser autoUser = ContextUtils.getLoggedInUser();
        if (BeanUtils.isNull(autoUser)) {
            throw new AuthenticationException();
        }

        List<Appointment> appointments;
        try {
            // Check if user is admin
            boolean isAdmin = autoUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN") || auth.getAuthority().equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                // Admin sees all appointments sorted by date/time
                appointments = appointmentRepository.findAllByOrderByAppointmentOnAscStartTimeAsc();
                LOGGER.debug("Admin user - returning all {} appointments", appointments.size());
            } else {
                // Regular user sees only their appointments sorted by date/time
                appointments = appointmentRepository.findByUserOrderByAppointmentOnAscStartTimeAsc(autoUser);
                LOGGER.debug("Regular user {} - returning {} appointments", autoUser.getEmail(), appointments.size());
            }
        } catch (Exception ex) {
            LOGGER.error(ex.getLocalizedMessage(), ex);
            appointments = new ArrayList<>();
        }

        LOGGER.debug("-getAppointmentsForCurrentUser(), count: {}", appointments.size());
        return appointments;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Appointment getById(Long id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new NoRecordFoundException("id:%d", id));
    }

    /**
     * @param filter
     * @return
     */
    @Override
    public List<Appointment> getByFilter(Filter<Appointment> filter) {
        return appointmentRepository.findAll();
    }

    /**
     * @param filter
     * @param pageable
     * @return
     */
    @Override
    public Page<Appointment> getByFilter(Filter<Appointment> filter, Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    /**
     * @param appointment
     * @return
     */
    @Override
    public Appointment update(Appointment appointment) {
        LOGGER.debug("+update({})", appointment);
        appointment = appointmentRepository.save(appointment);
        LOGGER.debug("-update(), appointment: {}", appointment);
        return appointment;
    }

    /**
     * @param appointments
     * @return
     */
    @Override
    public List<Appointment> update(List<Appointment> appointments) {
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Appointment delete(Long id) {
        return null;
    }


    /**
     * Filters the appointments for the users.
     *
     * @param appointments
     * @return
     */
    @PreFilter("principal.id == filterObject.user.id")
    public String saveAll(List<Appointment> appointments) {
        return appointments.stream().map(appointment -> appointment.getUser().getEmail())
            .collect(Collectors.joining(" "));
    }

    /**
     * @param autoUser
     * @return
     */
    public static Appointment createAppointment(final AutoUser autoUser) {
        final Appointment appointment = new Appointment();
        appointment.setUser(autoUser);
        return appointment;
    }
}
