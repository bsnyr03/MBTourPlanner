import at.fhtw.mbtourplanner.converter.DurationToIntervalConverter;
import at.fhtw.mbtourplanner.converter.DurationToIntervalDeserializer;
import at.fhtw.mbtourplanner.converter.DurationToIntervalSerializer;
import at.fhtw.mbtourplanner.repository.TourLogEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TourLogEntityTest {
    private DurationToIntervalConverter converter;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        converter = new DurationToIntervalConverter();
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Duration.class, new DurationToIntervalSerializer());
        module.addDeserializer(Duration.class, new DurationToIntervalDeserializer());
        mapper.registerModule(module);
        mapper.registerModule(new JavaTimeModule());
    }

    @Test void testBuilder() {
        TourLogEntity log = TourLogEntity.builder()
                .comment("Nice!").difficulty(2)
                .logDateTime(LocalDateTime.now())
                .totalDistance(5.0)
                .totalTime(Duration.ofHours(1))
                .rating(5).build();
        assertEquals("Nice!", log.getComment());
        assertEquals(2, log.getDifficulty());
    }
    @Test void testSettersAndGetters() {
        TourLogEntity log = new TourLogEntity();
        log.setComment("Test");
        assertEquals("Test", log.getComment());
    }

    @Test
    void converter_roundtrip() {
        Duration original = Duration.ofHours(3).plusMinutes(20).plusSeconds(45);
        String db = converter.convertToDatabaseColumn(original);
        assertThat(db).isEqualTo("03:20:45");

        Duration back = converter.convertToEntityAttribute(db);
        assertThat(back).isEqualTo(original);
    }

    @Test
    void jsonSerialize_deserialize() throws IOException {
        TourLogEntity entity = TourLogEntity.builder()
                .id(5L)
                .logDateTime(LocalDateTime.of(2025, 6, 30, 14, 15, 0))
                .comment("Nice")
                .difficulty(4)
                .totalDistance(7.5)
                .totalTime(Duration.ofMinutes(90))
                .rating(5)
                .build();

        String json = mapper.writeValueAsString(entity);
        assertThat(json)
                .contains("\"id\":5")
                .contains("\"logDateTime\":\"2025-06-30T14:15:00\"")
                .contains("\"totalTime\":\"01:30:00\"")
                .contains("\"rating\":5");

        TourLogEntity round = mapper.readValue(json, TourLogEntity.class);
        assertThat(round.getId()).isEqualTo(5L);
        assertThat(round.getLogDateTime()).isEqualTo(LocalDateTime.of(2025,6,30,14,15,0));
        assertThat(round.getTotalTime()).isEqualTo(Duration.ofMinutes(90));
        assertThat(round.getComment()).isEqualTo("Nice");
    }

    @Test
    void deserializer_invalidFormat_throws() {
        String badJson = "{\"totalTime\":\"invalid\"}";
        assertThrows(IOException.class, () -> mapper.readValue(badJson, TourLogEntity.class));
    }

    // Mixin to ignore TourEntity relation
    @JsonIgnoreProperties({"tour"})
    private static abstract class IgnoreTourMixin {}



}