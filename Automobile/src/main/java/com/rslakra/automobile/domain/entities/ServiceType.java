/**
 *
 */
package com.rslakra.automobile.domain.entities;

import com.rslakra.appsuite.spring.persistence.entity.NamedEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * @author Rohtash Lakra
 * @since 01-03-2020 2:07:39 PM
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "service_types")
public class ServiceType extends NamedEntity<Long> {

    @Column(name = "status")
    private String status;

}
