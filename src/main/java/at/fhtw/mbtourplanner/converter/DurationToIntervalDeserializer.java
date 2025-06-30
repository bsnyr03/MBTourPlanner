package at.fhtw.mbtourplanner.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Duration;
@Slf4j
public class DurationToIntervalDeserializer extends JsonDeserializer<Duration> {
    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        log.debug("Deserializing Duration from DB value='{}'", value);
        if (value == null || value.isEmpty()) return null;
        try {
            String[] parts = value.replace("-", "").split(":");
            if (parts.length != 3) throw new IllegalArgumentException("Invalid format");
            long hours = Long.parseLong(parts[0]);
            long minutes = Long.parseLong(parts[1]);
            long seconds = Long.parseLong(parts[2]);
            Duration duration = Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
            log.debug("Parsed Duration {} from value='{}'", duration, value);
            return value.startsWith("-") ? duration.negated() : duration;
        } catch (Exception e) {
            throw new IOException("Failed to parse Duration from '" + value + "'", e);
        }
    }
}