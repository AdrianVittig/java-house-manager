package org.university.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "apartment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Apartment extends BaseEntity{
    private String number;
    private BigDecimal area;

    // List of residents
    @OneToMany(mappedBy = "apartment")
    private List<Resident> residentList;

    // Building
    @ManyToOne(fetch = FetchType.LAZY)
    private Building building;
}
