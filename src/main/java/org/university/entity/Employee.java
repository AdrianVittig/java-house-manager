package org.university.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<Building> buildingList;

    // Company
    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    // Contract
    @OneToOne
    private Contract contract;
}
