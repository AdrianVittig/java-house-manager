package org.university.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.university.util.ResidentRole;

@Entity
@Table(name = "resident")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resident extends Person{
    @NotNull(message = "Resident role cannot be null")
    @Enumerated(EnumType.STRING)
    private ResidentRole role;

    // Apartment
    @ManyToOne(fetch = FetchType.LAZY)
    private Apartment apartment;

    // Uses elevator
    private boolean usesElevator;
}
