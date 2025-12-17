package org.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentWithDetailsDto {
    @NotNull(message = "Apartment id cannot be null")
    private Long id;

    @NotBlank(message = "Apartment number cannot be blank")
    private String number;

    @NotNull(message = "Apartment area cannot be null")
    @PositiveOrZero(message = "Apartment area cannot be negative")
    private BigDecimal area;

    private Long buildingId;

    private List<Long> residentIdsList = new ArrayList<>();
}
