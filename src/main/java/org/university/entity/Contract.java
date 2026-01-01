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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", nullable = false, unique = true)
    private Building building;

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;

        if (building != null && building.getContract() != this) {
            building.setContract(this);
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Override
    public String toString() {
        Long buildingId = (building != null ? building.getId() : null);
        Long employeeId = (employee != null ? employee.getId() : null);

        return "Contract{" +
                "id=" + getId() +
                ", number='" + number + '\'' +
                ", issueDate=" + issueDate +
                ", endDate=" + endDate +
                ", buildingId=" + buildingId +
                ", employeeId=" + employeeId +
                '}';
    }
}
