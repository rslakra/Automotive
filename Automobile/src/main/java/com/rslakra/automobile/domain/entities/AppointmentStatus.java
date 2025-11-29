package com.rslakra.automobile.domain.entities;

/**
 * Enum representing the possible statuses of an appointment.
 *
 * @author Rohtash Lakra
 */
public enum AppointmentStatus {

    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the enum value from string, or PENDING if not found.
     *
     * @param status
     * @return
     */
    public static AppointmentStatus fromString(String status) {
        if (status != null) {
            for (AppointmentStatus s : values()) {
                if (s.name().equalsIgnoreCase(status)) {
                    return s;
                }
            }
        }
        return PENDING;
    }
}

