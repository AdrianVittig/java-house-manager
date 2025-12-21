package org.university.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.annotation.Annotation;
import java.time.YearMonth;

@Converter(autoApply = false)
public class YearMonthConverter implements AttributeConverter<YearMonth, String> {


    @Override
    public String convertToDatabaseColumn(YearMonth yearMonth) {
        return yearMonth == null ? null : yearMonth.toString();
    }

    @Override
    public YearMonth convertToEntityAttribute(String s) {
        return s == null ? null : YearMonth.parse(s);
    }
}
