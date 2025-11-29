package com.rslakra.automobile.service;

import com.rslakra.appsuite.spring.service.AbstractService;
import com.rslakra.automobile.domain.entities.Appointment;

import java.util.List;

/**
 * @author Rohtash Lakra
 * @created 4/20/23 5:59 PM
 */
public interface AppointmentService extends AbstractService<Appointment, Long> {

    /**
     * Returns appointments for the current user.
     * Admin users see all appointments, regular users see only their own.
     * Results are sorted by date and time in ascending order.
     *
     * @return
     */
    List<Appointment> getAppointmentsForCurrentUser();
}

