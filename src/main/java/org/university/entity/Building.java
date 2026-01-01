package org.university.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.university.validators.NameRegex;

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
    @NameRegex
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Apartment> apartmentList = new ArrayList<>();

    @OneToOne(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Contract contract;

    public void setContract(Contract contract) {
        this.contract = contract;

        if (contract != null && contract.getBuilding() != this) {
            contract.setBuilding(this);
        }
    }

    public Contract getContract() {
        return contract;
    }

    @Override
    public String toString() {
        Long employeeId = (employee != null ? employee.getId() : null);
        Long contractId = (contract != null ? contract.getId() : null);

        return "Building{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", builtUpArea=" + builtUpArea +
                ", commonAreasPercentageOfBuiltUpArea=" + commonAreasPercentageOfBuiltUpArea +
                ", countOfFloors=" + countOfFloors +
                ", apartmentsPerFloor=" + apartmentsPerFloor +
                ", builtDate=" + builtDate +
                ", employeeId=" + employeeId +
                ", contractId=" + contractId +
                '}';
    }
}
