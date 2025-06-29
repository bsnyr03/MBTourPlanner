package at.fhtw.mbtourplanner;
import at.fhtw.mbtourplanner.converter.DurationToIntervalDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DurationToIntervalDeserializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Duration.class, new DurationToIntervalDeserializer());
        mapper.registerModule(module);
    }

    @Test
    void deserialize_validDurationString() throws IOException {
        Duration result = mapper.readValue("\"01:02:03\"", Duration.class);
        assertThat(result).isEqualTo(Duration.ofHours(1).plusMinutes(2).plusSeconds(3));
    }

    @Test
    void deserialize_negativeDurationString() throws IOException {
        Duration result = mapper.readValue("\"-02:03:04\"", Duration.class);
        assertThat(result).isEqualTo(Duration.ofHours(-2).minusMinutes(3).minusSeconds(4));
    }

    @Test
    void deserialize_emptyOrNullString_returnsNull() throws IOException {
        Duration nullResult = mapper.readValue("null", Duration.class);
        assertThat(nullResult).isNull();
        Duration emptyResult = mapper.readValue("\"\"", Duration.class);
        assertThat(emptyResult).isNull();
    }

    @Test
    void deserialize_invalidFormat_throwsIOException() {
        assertThrows(IOException.class, () -> mapper.readValue("\"invalid-format\"", Duration.class));
    }
}
