package org.university.validators;

import jakarta.validation.Payload;

public @interface NameRegex {
    String message() default "Invalid company name format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
