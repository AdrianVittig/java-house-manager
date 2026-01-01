package org.university.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = ValidPaymentValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPayment {
    String message() default "Invalid payment: paidAt is required when status is PAID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
