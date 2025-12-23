package org.university.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NameRegexValidator implements ConstraintValidator<NameRegex, String> {

    private static final Pattern pattern =
            Pattern.compile("^[A-Z][a-zA-Z0-9]+$");

    @Override
    public boolean isValid(String strValue, ConstraintValidatorContext constraintValidatorContext) {
        if(strValue == null || strValue.isBlank()){
            return true;
        }
        return pattern.matcher(strValue.trim()).matches();
    }
}
