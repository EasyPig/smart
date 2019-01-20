package com.smart.library.center.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Date;

import static com.smart.library.center.common.util.DateUtil.compatibilityDate;


public class LinuxTimeDeserializer extends JsonDeserializer<Date> {
	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String string = jp.getValueAsString();
        if (StringUtils.isNumeric(string)) {
            return compatibilityDate(Long.valueOf(string));
        } else {
            return ctxt.parseDate(string);
        }
    }
}
