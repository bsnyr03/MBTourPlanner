package at.fhtw.mbtourplanner.repository;

import at.fhtw.mbtourplanner.converter.DurationToIntervalConverter;
import at.fhtw.mbtourplanner.converter.DurationToIntervalDeserializer;
import at.fhtw.mbtourplanner.converter.DurationToIntervalSerializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mapper.addMixIn(TourLogEntity.class, IgnoreTourMixin.class);
    }

    @Test
    void testBuilderAndAccessors() {
        TourLogEntity log = TourLogEntity.builder()
                .comment("Nice!")
                .difficulty(2)
                .logDateTime(LocalDateTime.of(2025, 6, 30, 14, 15))
                .totalDistance(5.0)
                .totalTime(Duration.ofHours(1))
                .rating(5)
                .build();

        assertThat(log.getComment()).isEqualTo("Nice!");
        assertThat(log.getDifficulty()).isEqualTo(2);
        assertThat(log.getLogDateTime()).isEqualTo(LocalDateTime.of(2025, 6, 30, 14, 15));
    }

    @Test
    void converter_roundtrip() {
        Duration original = Duration.ofHours(3).plusMinutes(20).plusSeconds(45);
        String dbValue = converter.convertToDatabaseColumn(original);
        assertThat(dbValue).isEqualTo("03:20:45");

        Duration recovered = converter.convertToEntityAttribute(dbValue);
        assertThat(recovered).isEqualTo(original);
    }

    @Test
    void jsonSerialize_deserialize() throws IOException {
        TourLogEntity entity = TourLogEntity.builder()
                .id(5L)
                .tour(null)
                .logDateTime(LocalDateTime.of(2025, 6, 30, 14, 15))
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
        assertThat(round.getLogDateTime()).isEqualTo(LocalDateTime.of(2025, 6, 30, 14, 15));
        assertThat(round.getTotalTime()).isEqualTo(Duration.ofMinutes(90));
        assertThat(round.getComment()).isEqualTo("Nice");
    }

    @Test
    void deserializer_invalidFormat_throws() {
        String badJson = "{\"totalTime\":\"invalid\"}";
        assertThrows(IOException.class, () -> mapper.readValue(badJson, TourLogEntity.class));
    }

    @JsonIgnoreProperties({"tour"})
    private static abstract class IgnoreTourMixin {}
}