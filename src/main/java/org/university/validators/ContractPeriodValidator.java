package org.university.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.university.entity.Contract;

import java.time.LocalDate;

public class ContractPeriodValidator implements ConstraintValidator<ValidContractPeriod, Contract> {


    @Override
    public boolean isValid(Contract contract, ConstraintValidatorContext constraintValidatorContext) {
        if(contract == null) return true;
        LocalDate issueDate = contract.getIssueDate();
        LocalDate endDate = contract.getEndDate();
        if(endDate == null) return true;
        if(issueDate == null) return true;
        return endDate.isAfter(issueDate);
    }
}
