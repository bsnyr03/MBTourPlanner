package at.fhtw.mbtourplanner.converter;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class DurationToIntervalConverterTest {

    private final DurationToIntervalConverter converter = new DurationToIntervalConverter();

    @Test
    void convert_nullInput_returnsNull() {
        assertNull(converter.convertToDatabaseColumn(null));
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    void convertToDatabaseColumn_positiveDuration_formatsCorrectly() {
        Duration duration = Duration.ofHours(1).plusMinutes(2).plusSeconds(3);
        String dbValue = converter.convertToDatabaseColumn(duration);
        assertEquals("01:02:03", dbValue);
    }

    @Test
    void convertToEntityAttribute_positiveString_parsesCorrectly() {
        String dbValue = "01:02:03";
        Duration duration = converter.convertToEntityAttribute(dbValue);
        assertEquals(Duration.ofHours(1).plusMinutes(2).plusSeconds(3), duration);
    }

    @Test
    void convert_negativeDuration_formatsAndParsesCorrectly() {
        Duration negative = Duration.ofHours(-2).plusMinutes(-3).plusSeconds(-4);
        String dbValue = converter.convertToDatabaseColumn(negative);
        assertEquals("-02:03:04", dbValue);
        Duration parsed = converter.convertToEntityAttribute(dbValue);
        assertEquals(negative, parsed);
    }

    @Test
    void convertToEntityAttribute_invalidFormat_throwsException() {
        assertThrows(NumberFormatException.class, () -> converter.convertToEntityAttribute("invalid"));
    }
}