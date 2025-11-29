package com.rslakra.automobile.service;

import com.rslakra.appsuite.spring.service.AbstractService;
import com.rslakra.automobile.domain.entities.Schedule;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Schedule operations.
 *
 * @author Rohtash Lakra
 */
public interface ScheduleService extends AbstractService<Schedule, Long> {

    /**
     * Find all available schedules from today onwards.
     *
     * @return
     */
    List<Schedule> getAvailableSchedules();

    /**
     * Find all schedules from today onwards (for admin).
     *
     * @return
     */
    List<Schedule> getAllSchedules();

    /**
     * Find schedules for a specific date.
     *
     * @param date
     * @return
     */
    List<Schedule> getSchedulesForDate(LocalDate date);

    /**
     * Book a schedule.
     *
     * @param scheduleId
     * @return
     */
    Schedule bookSchedule(Long scheduleId);

    /**
     * Release a schedule.
     *
     * @param scheduleId
     * @return
     */
    Schedule releaseSchedule(Long scheduleId);

    /**
     * Generate default schedules for a date range.
     *
     * @param startDate
     * @param endDate
     * @return
     */
    List<Schedule> generateDefaultSchedules(LocalDate startDate, LocalDate endDate);
}

