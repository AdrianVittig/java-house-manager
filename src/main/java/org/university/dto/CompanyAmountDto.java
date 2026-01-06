package org.university.dto;

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
public class CompanyAmountDto {
    private Long companyId;
    private String companyName;

    @PositiveOrZero(message = "Company amount cannot be negative")
    private BigDecimal amount;

    @Override
    public String toString() {
        return "CompanyAmountDto{" +
                "companyId=" + companyId +
                ", companyName='" + companyName + '\'' +
                ", amount=" + amount +
                '}';
    }

}
