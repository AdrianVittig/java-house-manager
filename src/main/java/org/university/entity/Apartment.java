package org.university.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "apartment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Apartment extends BaseEntity{
    @NotBlank(message = "Apartment number cannot be blank")
    private String number;

    @NotNull(message = "Apartment area cannot be null")
    @PositiveOrZero(message = "Apartment area cannot be negative")
    private BigDecimal area;

    private boolean hasPet;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resident> residentList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    @OneToMany(mappedBy = "apartment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoiceList = new ArrayList<>();

    @Override
    public String toString() {
        Long buildingId = (building != null ? building.getId() : null);

        return "Apartment{" +
                "id=" + getId() +
                ", number='" + number + '\'' +
                ", area=" + area +
                ", hasPet=" + hasPet +
                ", buildingId=" + buildingId +
                '}';
    }
}
