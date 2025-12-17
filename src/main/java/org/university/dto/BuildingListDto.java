package org.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuildingListDto {
    @NotNull(message = "Building id cannot be null")
    private Long id;
    @NotBlank(message = "Building name cannot be blank")
    private String name;
    @NotNull(message = "Count of floors cannot be null")
    @PositiveOrZero(message = "Count of floors cannot be negative")
    private int countOfFloors;
    @NotNull(message = "Apartments per floor cannot be null")
    @PositiveOrZero(message = "Apartments per floor cannot be negative")
    private int apartmentsPerFloor;
    @NotNull(message = "Built date cannot be null")
    private LocalDate builtDate;
}
