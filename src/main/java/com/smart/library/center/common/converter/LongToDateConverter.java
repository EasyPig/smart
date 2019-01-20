package com.smart.library.center.common.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Date;

import static com.smart.library.center.common.util.DateUtil.compatibilityDate;


/**
 * Created by Jerolin on 6/27/2014.
 */
@ReadingConverter
public class LongToDateConverter implements Converter<Long, Date> {
    @Override
    public Date convert(Long source) {
        if (source != null) {
            return compatibilityDate(source);
        }
        return null;
    }
}
