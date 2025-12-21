package org.university.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.university.entity.Apartment;
import org.university.entity.Payment;
import org.university.util.PaymentStatus;
import org.university.util.YearMonthConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceWithDetailsDto {
    @NotNull(message = "Invoice id cannot be null")
    private Long id;
    @NotNull(message = "Billing month cannot be null")
    private YearMonth billingMonth;

    @NotNull(message = "Invoice due date cannot be null")
    private LocalDate dueDate;

    @PositiveOrZero(message = "Total amount cannot be negative")
    @NotNull(message = "Total amount cannot be null")
    private BigDecimal totalAmount;

    @NotNull(message = "Payment status cannot be null")
    private PaymentStatus paymentStatus = PaymentStatus.NOT_PAID;

    private Long apartmentId;

    private Long paymentId;
}
