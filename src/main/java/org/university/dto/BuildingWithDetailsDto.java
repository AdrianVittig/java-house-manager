package org.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuildingWithDetailsDto {
    @NotNull(message = "Building id cannot be null")
    private Long id;
    @NotBlank(message = "Building name cannot be blank")
    private String name;
    @NotBlank(message = "Address cannot be blank")
    private String address;
    @NotNull(message = "Built up area cannot be null")
    @PositiveOrZero(message = "Built up area cannot be negative")
    private BigDecimal builtUpArea;
    @NotNull(message = "Common areas percentage of built up area cannot be null")
    @PositiveOrZero(message = "Common areas percentage of built up area cannot be negative")
    private BigDecimal commonAreasPercentageOfBuiltUpArea;
    @NotNull(message = "Count of floors cannot be null")
    @PositiveOrZero(message = "Count of floors cannot be negative")
    private int countOfFloors;
    @NotNull(message = "Apartments per floor cannot be null")
    @PositiveOrZero(message = "Apartments per floor cannot be negative")
    private Integer apartmentsPerFloor;
    @NotNull(message = "Built date cannot be null")
    private LocalDate builtDate;

    private Long employeeId;

    private List<Long> apartmentIdsList = new ArrayList<>();

    private Long contractId;
}
