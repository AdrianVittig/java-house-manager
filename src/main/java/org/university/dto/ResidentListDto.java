package org.university.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.university.util.ResidentRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResidentListDto {
    @NotNull(message = "Resident id cannot be null")
    private Long id;
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @NotNull(message = "Age cannot be null")
    private int age;

    @NotNull(message = "Resident role cannot be null")
    private ResidentRole role;

    @NotNull(message = "Has pet cannot be null")
    private boolean hasPet;
}
