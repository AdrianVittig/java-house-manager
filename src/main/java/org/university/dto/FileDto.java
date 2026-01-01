package org.university.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.university.util.PaymentStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long invoiceId;
    private YearMonth billingMonth;
    private Long buildingId;

    private String companyName;
    private String employeeFirstName;
    private String employeeLastName;

    private String buildingName;
    private String buildingAddress;

    private String apartmentNumber;

    private BigDecimal amount;
    private LocalDateTime paidAt;

    private PaymentStatus paymentStatus;

    @Override
    public String toString() {
        return "FileDto{" +
                "invoiceId=" + invoiceId +
                ", buildingId=" + buildingId +
                ", billingMonth=" + billingMonth +
                ", amount=" + amount +
                ", paymentStatus=" + paymentStatus +
                ", paidAt=" + paidAt +
                ", companyName='" + companyName + '\'' +
                ", employeeName='" + employeeFirstName + " " + employeeLastName + '\'' +
                '}';
    }
}
