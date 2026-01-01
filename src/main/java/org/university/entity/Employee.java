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
    @OneToMany(mappedBy = "employee")
    private List<Building> buildingList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotNull(message = "Fee collecting date cannot be null")
    private LocalDate feeCollectingDate;

    @OneToMany(mappedBy = "employee")
    private List<Contract> contractList = new ArrayList<>();

    @Override
    public String toString() {
        Long companyId = (company != null ? company.getId() : null);

        return "Employee{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", age=" + getAge() +
                ", feeCollectingDate=" + feeCollectingDate +
                ", companyId=" + companyId +
                '}';
    }
}
