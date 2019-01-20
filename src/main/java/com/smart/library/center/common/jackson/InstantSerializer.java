package com.smart.library.center.common.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

/**
 *
 *
 */
public class InstantSerializer extends JsonSerializer<Instant> {
    @Override public void serialize(Instant value,
                                    JsonGenerator generator,
                                    SerializerProvider provider) throws IOException, JsonProcessingException {
        if (provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
            BigDecimal decimal = DecimalUtils.toBigDecimal(value.getEpochSecond(), value.getNano());
            generator.writeNumber(decimal.stripTrailingZeros());
            return;
        }
        generator.writeNumber(value.toEpochMilli());
    }
}
