package org.university.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "contract")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contract extends BaseEntity{
    private String number;
    private LocalDate issueDate;
    private LocalDate endDate;

    // Building
    @OneToOne
    private Building building;

    // Employee
    @OneToOne
    private Employee employee;
}
