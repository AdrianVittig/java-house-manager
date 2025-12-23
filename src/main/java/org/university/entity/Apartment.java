package org.university.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.university.validators.ApartmentNumberRegex;

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
    @ApartmentNumberRegex
    private String number;

    @NotNull(message = "Apartment area cannot be null")
    @PositiveOrZero(message = "Apartment area cannot be negative")
    private BigDecimal area;

    @NotNull(message = "Has pet cannot be null")
    private boolean hasPet;

    // List of residents
    @OneToMany(mappedBy = "apartment")
    private List<Resident> residentList = new ArrayList<>();

    // Building
    @ManyToOne(fetch = FetchType.LAZY)
    private Building building;

    @OneToMany(mappedBy = "apartment")
    private List<Invoice> invoiceList = new ArrayList<>();
}
