package org.university.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeBuildingsCountDto {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private Long buildingsCount;

    @Override
    public String toString() {
        return "EmployeeBuildingsCountDto{" +
                "employeeId=" + employeeId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", buildingsCount=" + buildingsCount +
                '}';
    }

}
