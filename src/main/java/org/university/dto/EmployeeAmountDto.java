package org.university.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAmountDto {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private BigDecimal amount;

    @Override
    public String toString() {
        return "EmployeeAmountDto{" +
                "employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", amount=" + amount +
                '}';
    }

}
