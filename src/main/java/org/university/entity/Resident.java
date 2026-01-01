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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    private boolean usesElevator;

    @Override
    public String toString() {
        Long apartmentId = (apartment != null ? apartment.getId() : null);

        return "Resident{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", age=" + getAge() +
                ", role=" + role +
                ", usesElevator=" + usesElevator +
                ", apartmentId=" + apartmentId +
                '}';
    }
}
