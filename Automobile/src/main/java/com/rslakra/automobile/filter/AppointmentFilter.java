package com.rslakra.automobile.filter;

import com.devamatre.appsuite.spring.filter.AbstractFilter;
import com.rslakra.automobile.domain.entities.Appointment;

import java.util.Map;

/**
 * @author Rohtash Lakra
 * @created 4/26/23 1:05 PM
 */
public final class AppointmentFilter extends AbstractFilter<Appointment> {

    /**
     * @param allParams
     */
    public AppointmentFilter(Map<String, Object> allParams) {
        super(allParams);
    }
}
