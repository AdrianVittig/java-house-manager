package org.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CompanyWithDetailsDto {
    @NotNull(message = "Company id cannot be null")
    private Long id;
    @NotBlank(message = "Company name cannot be blank")
    private String name;
    @NotNull(message = "Company revenue cannot be null")
    private BigDecimal revenue;

    private List<Long> employeeIdsList = new ArrayList<>();
}
