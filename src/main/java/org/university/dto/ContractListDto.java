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
public class ContractListDto {
    @NotNull(message = "Contract id cannot be null")
    private Long id;
    @NotBlank(message = "Contract number cannot be blank")
    private String number;
    @NotNull(message = "Contract issue date cannot be null")
    private LocalDate issueDate;
    @NotNull(message = "Contract end date cannot be null")
    private LocalDate endDate;

    @Override
    public String toString() {
        return "ContractListDto{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", issueDate=" + issueDate +
                ", endDate=" + endDate +
                '}';
    }

}
