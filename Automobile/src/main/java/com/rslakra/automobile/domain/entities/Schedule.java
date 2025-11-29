package com.rslakra.automobile.domain.entities;

import com.rslakra.appsuite.core.ToString;
import com.rslakra.appsuite.spring.persistence.entity.NamedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity representing an available schedule/time slot for appointments.
 *
 * @author Rohtash Lakra
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "schedules")
public class Schedule extends NamedEntity<Long> {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "available", nullable = false)
    private boolean available = true;

    @Column(name = "max_appointments")
    private Integer maxAppointments = 1;

    @Column(name = "current_appointments")
    private Integer currentAppointments = 0;

    /**
     * Constructor with date and times.
     *
     * @param scheduleDate
     * @param startTime
     * @param endTime
     */
    public Schedule(LocalDate scheduleDate, LocalTime startTime, LocalTime endTime) {
        this.scheduleDate = scheduleDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns true if the schedule has availability.
     *
     * @return
     */
    public boolean hasAvailability() {
        return available && (currentAppointments < maxAppointments);
    }

    /**
     * Books an appointment in this schedule.
     */
    public void bookSchedule() {
        if (hasAvailability()) {
            currentAppointments++;
            if (currentAppointments >= maxAppointments) {
                available = false;
            }
        }
    }

    /**
     * Releases an appointment from this schedule.
     */
    public void releaseSchedule() {
        if (currentAppointments > 0) {
            currentAppointments--;
            available = true;
        }
    }

    /**
     * Returns the display time range.
     *
     * @return
     */
    public String getTimeRange() {
        return String.format("%s - %s", startTime, endTime);
    }

    @Override
    public String toString() {
        return ToString.of(Schedule.class)
            .add("id", getId())
            .add("scheduleDate", scheduleDate)
            .add("startTime", startTime)
            .add("endTime", endTime)
            .add("available", available)
            .toString();
    }
}

