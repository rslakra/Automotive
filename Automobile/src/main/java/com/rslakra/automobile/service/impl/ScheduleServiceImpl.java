package com.rslakra.automobile.service.impl;

import com.rslakra.appsuite.spring.exception.NoRecordFoundException;
import com.rslakra.appsuite.spring.filter.Filter;
import com.rslakra.appsuite.spring.persistence.ServiceOperation;
import com.rslakra.appsuite.spring.service.AbstractServiceImpl;
import com.rslakra.automobile.domain.entities.Schedule;
import com.rslakra.automobile.domain.repositories.ScheduleRepository;
import com.rslakra.automobile.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for Schedule operations.
 *
 * @author Rohtash Lakra
 */
@Service
public class ScheduleServiceImpl extends AbstractServiceImpl<Schedule, Long> implements ScheduleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public Schedule validate(ServiceOperation operation, Schedule schedule) {
        return schedule;
    }

    @Override
    public List<Schedule> getAvailableSchedules() {
        LOGGER.debug("+getAvailableSchedules()");
        List<Schedule> schedules = scheduleRepository.findAvailableFromDate(LocalDate.now());
        LOGGER.debug("-getAvailableSchedules(), count: {}", schedules.size());
        return schedules;
    }

    @Override
    public List<Schedule> getAllSchedules() {
        LOGGER.debug("+getAllSchedules()");
        List<Schedule> schedules = scheduleRepository.findAllFromDate(LocalDate.now());
        LOGGER.debug("-getAllSchedules(), count: {}", schedules.size());
        return schedules;
    }

    @Override
    public List<Schedule> getSchedulesForDate(LocalDate date) {
        LOGGER.debug("+getSchedulesForDate({})", date);
        List<Schedule> schedules = scheduleRepository.findByScheduleDateOrderByStartTime(date);
        LOGGER.debug("-getSchedulesForDate(), count: {}", schedules.size());
        return schedules;
    }

    @Override
    @Transactional
    public Schedule bookSchedule(Long scheduleId) {
        LOGGER.debug("+bookSchedule({})", scheduleId);
        Schedule schedule = getById(scheduleId);
        schedule.bookSchedule();
        schedule = scheduleRepository.save(schedule);
        LOGGER.debug("-bookSchedule(), schedule: {}", schedule);
        return schedule;
    }

    @Override
    @Transactional
    public Schedule releaseSchedule(Long scheduleId) {
        LOGGER.debug("+releaseSchedule({})", scheduleId);
        Schedule schedule = getById(scheduleId);
        schedule.releaseSchedule();
        schedule = scheduleRepository.save(schedule);
        LOGGER.debug("-releaseSchedule(), schedule: {}", schedule);
        return schedule;
    }

    @Override
    @Transactional
    public List<Schedule> generateDefaultSchedules(LocalDate startDate, LocalDate endDate) {
        LOGGER.debug("+generateDefaultSchedules({}, {})", startDate, endDate);
        List<Schedule> generatedSchedules = new ArrayList<>();

        // Default time slots: 9 AM, 10 AM, 11 AM, 1 PM, 2 PM, 3 PM, 4 PM
        LocalTime[] defaultTimes = {
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            LocalTime.of(13, 0),
            LocalTime.of(14, 0),
            LocalTime.of(15, 0),
            LocalTime.of(16, 0)
        };

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            // Skip weekends (Saturday=6, Sunday=7)
            if (currentDate.getDayOfWeek().getValue() < 6) {
                for (LocalTime startTime : defaultTimes) {
                    Schedule schedule = new Schedule();
                    schedule.setScheduleDate(currentDate);
                    schedule.setStartTime(startTime);
                    schedule.setEndTime(startTime.plusHours(1));
                    schedule.setAvailable(true);
                    schedule.setMaxAppointments(2); // Allow 2 appointments per slot
                    schedule.setCurrentAppointments(0);
                    generatedSchedules.add(scheduleRepository.save(schedule));
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        LOGGER.debug("-generateDefaultSchedules(), generated: {}", generatedSchedules.size());
        return generatedSchedules;
    }

    @Override
    public List<Schedule> getAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public Schedule getById(Long id) {
        return scheduleRepository.findById(id)
            .orElseThrow(() -> new NoRecordFoundException("Schedule not found with id: " + id));
    }

    @Override
    public List<Schedule> getByFilter(Filter<Schedule> filter) {
        return getAll();
    }

    @Override
    public Page<Schedule> getByFilter(Filter<Schedule> filter, Pageable pageable) {
        return scheduleRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Schedule create(Schedule schedule) {
        LOGGER.debug("+create({})", schedule);
        schedule = scheduleRepository.save(schedule);
        LOGGER.debug("-create(), schedule: {}", schedule);
        return schedule;
    }

    @Override
    public List<Schedule> create(List<Schedule> schedules) {
        return scheduleRepository.saveAll(schedules);
    }

    @Override
    @Transactional
    public Schedule update(Schedule schedule) {
        LOGGER.debug("+update({})", schedule);
        schedule = scheduleRepository.save(schedule);
        LOGGER.debug("-update(), schedule: {}", schedule);
        return schedule;
    }

    @Override
    public List<Schedule> update(List<Schedule> schedules) {
        return scheduleRepository.saveAll(schedules);
    }

    @Override
    @Transactional
    public Schedule delete(Long id) {
        LOGGER.debug("+delete({})", id);
        Schedule schedule = getById(id);
        scheduleRepository.delete(schedule);
        LOGGER.debug("-delete()");
        return schedule;
    }
}
