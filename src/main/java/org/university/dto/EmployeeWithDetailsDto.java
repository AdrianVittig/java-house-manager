package org.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWithDetailsDto {
    @NotNull(message = "Employee id cannot be null")
    private Long id;
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @NotNull(message = "Age cannot be null")
    private Integer age;

    @NotNull(message = "Fee collecting date cannot be null")
    private LocalDate feeCollectingDate;

    private List<Long> buildingIdsList = new ArrayList<>();

    private Long companyId;

    private List<Long> contractIdsList;

    @Override
    public String toString() {
        return "EmployeeWithDetailsDto{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", feeCollectingDate=" + feeCollectingDate +
                ", companyId=" + companyId +
                ", buildingIds(count)=" + (buildingIdsList == null ? 0 : buildingIdsList.size()) +
                ", buildingIds=" + buildingIdsList +
                ", contractIds(count)=" + (contractIdsList == null ? 0 : contractIdsList.size()) +
                ", contractIds=" + contractIdsList +
                '}';
    }

}
