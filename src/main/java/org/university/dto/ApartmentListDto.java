package org.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentListDto {
    @NotNull(message = "Apartment id cannot be null")
    private Long id;

    @NotBlank(message = "Apartment number cannot be blank")
    private String number;

    @NotNull(message = "Apartment area cannot be null")
    @PositiveOrZero(message = "Apartment area cannot be negative")
    private BigDecimal area;

    @NotNull(message = "Has pet cannot be null")
    private boolean hasPet;

    @Override
    public String toString() {
        return "ApartmentListDto{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", area=" + area +
                ", hasPet=" + hasPet +
                '}';
    }
}
