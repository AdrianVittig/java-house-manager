package org.university.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.university.entity.Payment;
import org.university.util.PaymentStatus;

public class ValidPaymentValidator implements ConstraintValidator<ValidPayment, Payment> {
    @Override
    public boolean isValid(Payment payment, ConstraintValidatorContext constraintValidatorContext) {
        if(payment == null) return true;

        if(payment.getPaymentStatus() == null) return true;

        boolean isPaid = payment.getPaymentStatus() == PaymentStatus.PAID;

        if(isPaid && payment.getPaidAt() == null) return false;

        return true;
    }
}
