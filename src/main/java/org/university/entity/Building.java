package org.university.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "building")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Building extends BaseEntity{
    @NotBlank(message = "Building name cannot be blank")
    private String name;
    @NotBlank(message = "Address cannot be blank")
    private String address;
    @NotNull(message = "Built up area cannot be null")
    @PositiveOrZero(message = "Built up area cannot be negative")
    private BigDecimal builtUpArea;
    @NotNull(message = "Common areas percentage of built up area cannot be null")
    @PositiveOrZero(message = "Common areas percentage of built up area cannot be negative")
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "1.0", inclusive = true)
    private BigDecimal commonAreasPercentageOfBuiltUpArea;
    @NotNull(message = "Count of floors cannot be null")
    @Positive(message = "Count of floors cannot be negative")
    private Integer countOfFloors;
    @NotNull(message = "Apartments per floor cannot be null")
    @Positive(message = "Apartments per floor cannot be negative")
    private Integer apartmentsPerFloor;
    @NotNull(message = "Built date cannot be null")
    private LocalDate builtDate;

    // Employee
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    // Apartments
    @OneToMany(mappedBy = "building")
    private List<Apartment> apartmentList = new ArrayList<>();

    // Contract
    @OneToOne
    private Contract contract;
}
