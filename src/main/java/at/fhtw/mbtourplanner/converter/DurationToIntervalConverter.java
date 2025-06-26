package at.fhtw.mbtourplanner.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Duration;

@Converter(autoApply = true)
public class DurationToIntervalConverter implements AttributeConverter<Duration, String> {
    @Override
    public String convertToDatabaseColumn(Duration duration) {
        if (duration == null) return null;
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String formatted = String.format(
                "%02d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60
        );
        return seconds < 0 ? "-" + formatted : formatted;
    }

    @Override
    public Duration convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        String[] parts = dbData.replace("-", "").split(":");
        long hours = Long.parseLong(parts[0]);
        long minutes = Long.parseLong(parts[1]);
        long seconds = Long.parseLong(parts[2]);
        Duration duration = Duration.ofHours(hours).plusMinutes(minutes).plusSeconds(seconds);
        return dbData.startsWith("-") ? duration.negated() : duration;
    }
}
