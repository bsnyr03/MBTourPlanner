package at.fhtw.mbtourplanner.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.Duration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DurationToIntervalSerializer extends JsonSerializer<Duration> {
    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        log.debug("serialize: input duration={}", value);
        if (value == null) {
            gen.writeNull();
            return;
        }
        long seconds = value.getSeconds();
        long absSeconds = Math.abs(seconds);
        String formatted = String.format(
                "%02d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60
        );
        log.debug("serialize: formatted='{}'", formatted);
        gen.writeString(seconds < 0 ? "-" + formatted : formatted);
    }
}