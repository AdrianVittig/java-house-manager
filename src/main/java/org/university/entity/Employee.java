package org.university.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends Person{
    // Buildings
    @OneToMany(mappedBy = "employee")
    private List<Building> buildingList = new ArrayList<>();

    // Company
    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    // FeeCollectingDate
    @NotNull(message = "Fee collecting date cannot be null")
    private LocalDate feeCollectingDate;

    // Contract
    @OneToMany(mappedBy = "employee")
    private List<Contract> contractList = new ArrayList<>();
}
