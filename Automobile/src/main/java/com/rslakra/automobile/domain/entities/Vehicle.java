package com.rslakra.automobile.domain.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * @author Rohtash Lakra
 * @since 09-16-2019 1:38:52 PM
 */
@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Vehicle {

    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    @Column(name = "\"year\"")
    private Short year;

}
