package com.rslakra.automobile.domain.repositories;

import com.rslakra.automobile.domain.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Schedule entity.
 *
 * @author Rohtash Lakra
 */
@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /**
     * Find all schedules for a specific date.
     *
     * @param scheduleDate
     * @return
     */
    List<Schedule> findByScheduleDateOrderByStartTime(LocalDate scheduleDate);

    /**
     * Find all available schedules.
     *
     * @return
     */
    List<Schedule> findByAvailableTrueOrderByScheduleDateAscStartTimeAsc();

    /**
     * Find available schedules from a specific date onwards.
     *
     * @param fromDate
     * @return
     */
    @Query("SELECT s FROM Schedule s WHERE s.scheduleDate >= :fromDate AND s.available = true ORDER BY s.scheduleDate, s.startTime")
    List<Schedule> findAvailableFromDate(@Param("fromDate") LocalDate fromDate);

    /**
     * Find all schedules from a specific date onwards.
     *
     * @param fromDate
     * @return
     */
    @Query("SELECT s FROM Schedule s WHERE s.scheduleDate >= :fromDate ORDER BY s.scheduleDate, s.startTime")
    List<Schedule> findAllFromDate(@Param("fromDate") LocalDate fromDate);
}

