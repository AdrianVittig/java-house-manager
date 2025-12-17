package org.university.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "building")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Building extends BaseEntity{
    private String name;
    private int countOfFloors;
    private int apartmentsPerFloor;
    private LocalDate builtDate;

    // Employee
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    // Apartments
    @OneToMany(mappedBy = "building")
    private List<Apartment> apartmentList;

    // Contract
    @OneToOne
    private Contract contract;
}
