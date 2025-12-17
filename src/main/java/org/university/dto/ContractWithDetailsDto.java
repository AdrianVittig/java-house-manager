package org.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractWithDetailsDto {
    @NotNull(message = "Contract id cannot be null")
    private Long id;
    @NotBlank(message = "Contract number cannot be blank")
    private String number;
    @NotBlank(message = "Contract issue date cannot be blank")
    private LocalDate issueDate;
    @NotBlank(message = "Contract end date cannot be blank")
    private LocalDate endDate;

    private Long buildingId;

    private Long employeeId;
}
