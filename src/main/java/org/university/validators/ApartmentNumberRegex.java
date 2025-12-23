package org.university.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ApartmentNumberRegexValidator.class)
public @interface ApartmentNumberRegex {
    String message() default "Invalid apartment number format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
