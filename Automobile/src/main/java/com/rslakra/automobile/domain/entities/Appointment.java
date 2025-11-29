package com.rslakra.automobile.domain.entities;

import com.rslakra.appsuite.core.BeanUtils;
import com.rslakra.appsuite.spring.persistence.entity.AbstractEntity;
import com.rslakra.automobile.domain.entities.converter.LocalDateConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * @author Rohtash Lakra
 * @since 09-16-2019 1:38:47 PM
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "appointments")
public class Appointment extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private AutoUser user;

    @Embedded
    private Vehicle vehicle;

    @Convert(converter = LocalDateConverter.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "appointment_on")
    private LocalDate appointmentOn;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "start_time")
    private LocalTime startTime;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "end_time")
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "appointment_services",
        joinColumns = @JoinColumn(name = "appointment_id"),
        inverseJoinColumns = @JoinColumn(name = "service_type_id")
    )
    private List<ServiceType> services = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AppointmentStatus status;

    /**
     * @param serviceType
     */
    public void addService(ServiceType serviceType) {
        if (BeanUtils.isNull(services)) {
            services = new ArrayList<>();
        }

        getServices().add(serviceType);
    }

    /**
     * Returns formatted time range.
     * @return
     */
    public String getTimeRange() {
        if (startTime != null && endTime != null) {
            return String.format("%s - %s", startTime, endTime);
        }
        return "";
    }

}
