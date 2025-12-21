package org.university.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.university.util.PaymentStatus;
import org.university.util.YearMonthConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice extends BaseEntity{
    @NotNull(message = "Billing month cannot be null")
    @Convert(converter = YearMonthConverter.class)
    private YearMonth billingMonth;

    @NotNull(message = "Invoice due date cannot be null")
    private LocalDate dueDate;

    @PositiveOrZero(message = "Total amount cannot be negative")
    @NotNull(message = "Total amount cannot be null")
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Payment status cannot be null")
    private PaymentStatus paymentStatus = PaymentStatus.NOT_PAID;

    @ManyToOne(fetch = FetchType.LAZY)
    private Apartment apartment;

    @OneToOne(mappedBy = "invoice")
    private Payment payment;
}
