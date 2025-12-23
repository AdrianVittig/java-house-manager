package org.university.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ApartmentNumberRegexValidator implements ConstraintValidator<ApartmentNumberRegex, String> {

    private static final Pattern pattern = Pattern.compile("^[0-9]+$");


    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
       if(s == null || s.isBlank()) return true;
       return pattern.matcher(s.trim()).matches();
    }
}
