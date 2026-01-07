package org.university.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.university.util.PaymentStatus;
import org.university.validators.ValidPayment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidPayment
public class Payment extends BaseEntity {
    @PositiveOrZero(message = "Amount cannot be negative")
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.NOT_PAID;
    private LocalDateTime paidAt;

    @PrePersist
    private void prePersist() {
        if (paidAt == null && paymentStatus == PaymentStatus.PAID) {
            paidAt = LocalDateTime.now();
        }
    }


    @NotNull(message = "Invoice cannot be null")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false, unique = true)
    private Invoice invoice;

    @Override
    public String toString() {
        Long invoiceId = (invoice != null ? invoice.getId() : null);

        return "Payment{" +
                "id=" + getId() +
                ", amount=" + amount +
                ", paymentStatus=" + paymentStatus +
                ", paidAt=" + paidAt +
                ", invoiceId=" + invoiceId +
                '}';
    }
}
