package org.university.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.university.util.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentListDto {
    @NotNull(message = "Payment id cannot be null")
    private Long id;
    @PositiveOrZero(message = "Amount cannot be negative")
    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;
    @NotNull(message = "Payment status cannot be null")
    private PaymentStatus paymentStatus = PaymentStatus.NOT_PAID;
    @NotNull(message = "Paid at cannot be null")
    private LocalDateTime paidAt;

    @Override
    public String toString() {
        return "PaymentListDto{" +
                "id=" + id +
                ", amount=" + amount +
                ", paymentStatus=" + paymentStatus +
                ", paidAt=" + paidAt +
                '}';
    }

}
