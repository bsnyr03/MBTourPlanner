package at.fhtw.mbtourplanner;
import at.fhtw.mbtourplanner.converter.DurationToIntervalSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class DurationToIntervalSerializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Duration.class, new DurationToIntervalSerializer());
        mapper.registerModule(module);
    }

    @Test
    void serialize_nullValue_writesNull() throws Exception {
        // Serializing a null Duration should yield JSON null
        String json = mapper.writeValueAsString((Duration) null);
        assertThat(json).isEqualTo("null");
    }

    @Test
    void serialize_zeroDuration_writesAllZeros() throws Exception {
        Duration zero = Duration.ofSeconds(0);
        String json = mapper.writeValueAsString(zero);
        assertThat(json).isEqualTo("\"00:00:00\"");
    }

    @Test
    void serialize_positiveDuration_writesFormattedString() throws Exception {
        Duration duration = Duration.ofHours(1).plusMinutes(2).plusSeconds(3);
        String json = mapper.writeValueAsString(duration);
        assertThat(json).isEqualTo("\"01:02:03\"");
    }

    @Test
    void serialize_negativeDuration_writesFormattedStringWithMinus() throws Exception {
        Duration negative = Duration.ofHours(-2).minusMinutes(3).minusSeconds(4);
        String json = mapper.writeValueAsString(negative);
        assertThat(json).isEqualTo("\"-02:03:04\"");
    }
}

