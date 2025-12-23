package org.university.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ContractPeriodValidator.class)
public @interface ValidContractPeriod {
    String message() default "Contract end date must be after issue date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
