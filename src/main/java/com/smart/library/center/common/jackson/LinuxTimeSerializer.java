package com.smart.library.center.common.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

import static com.smart.library.center.common.util.DateUtil.toEpochSecond;


/**
 *
 *
 */
public class LinuxTimeSerializer extends JsonSerializer<Date> {
    @Override public void serialize(Date value,
                                    JsonGenerator gen,
                                    SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (value != null) {
            gen.writeNumber(toEpochSecond(value));
        }
    }


}
