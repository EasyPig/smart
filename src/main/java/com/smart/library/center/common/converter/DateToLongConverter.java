package com.smart.library.center.common.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Date;

@ReadingConverter
public class DateToLongConverter implements Converter<Date, Long> {
    @Override public Long convert(Date source) {
        if (source == null) {
            return null;
        }
        return source.toInstant().getEpochSecond();
    }
}
