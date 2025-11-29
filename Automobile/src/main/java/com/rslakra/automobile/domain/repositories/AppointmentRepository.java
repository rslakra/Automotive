package com.rslakra.automobile.domain.repositories;

import com.rslakra.automobile.domain.entities.Appointment;
import com.rslakra.automobile.domain.entities.AutoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Rohtash Lakra
 * @since 09-16-2019 1:39:44 PM
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Returns the appointments of the given user.
     *
     * @param user
     * @return
     */
    List<Appointment> findByUser(AutoUser user);

    /**
     * Returns the appointments of the given user, sorted by date and start time ascending.
     *
     * @param user
     * @return
     */
    List<Appointment> findByUserOrderByAppointmentOnAscStartTimeAsc(AutoUser user);

    /**
     * Returns all appointments sorted by date and start time ascending (for admin).
     *
     * @return
     */
    List<Appointment> findAllByOrderByAppointmentOnAscStartTimeAsc();
}

