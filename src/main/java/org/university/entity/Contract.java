package org.university.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.university.validators.ValidContractPeriod;

import java.time.LocalDate;

@Entity
@Table(name = "contract")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidContractPeriod
public class Contract extends BaseEntity{
    private String number;

    @PastOrPresent(message = "Contract issue date cannot be in the future")
    private LocalDate issueDate;

    private LocalDate endDate;

    // Building
    @OneToOne
    private Building building;

    // Employee
    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;
}
