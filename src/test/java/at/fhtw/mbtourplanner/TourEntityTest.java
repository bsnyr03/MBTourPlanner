import at.fhtw.mbtourplanner.converter.DurationToIntervalConverter;
import at.fhtw.mbtourplanner.converter.DurationToIntervalDeserializer;
import at.fhtw.mbtourplanner.converter.DurationToIntervalSerializer;
import at.fhtw.mbtourplanner.repository.TourEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TourEntityTest {

    private ObjectMapper mapper;
    private DurationToIntervalConverter converter;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Duration.class, new DurationToIntervalSerializer());
        module.addDeserializer(Duration.class, new DurationToIntervalDeserializer());
        mapper.registerModule(module);

        converter = new DurationToIntervalConverter();
    }

    @Test void testBuilder() {
        TourEntity tour = TourEntity.builder()
                .name("Test Tour").description("Desc")
                .fromLocation("A").toLocation("B")
                .transportType("bike").distance(10.0)
                .estimatedTime(Duration.ofHours(1)).routeImageUrl("url").build();
        assertEquals("Test Tour", tour.getName());
        assertEquals("A", tour.getFromLocation());
    }

    @Test void testToString() {
        TourEntity tour = TourEntity.builder().name("Tour").build();
        assertTrue(tour.toString().contains("Tour"));
    }

    @Test
    void jpaConverter_roundtrip() {
        Duration original = Duration.ofHours(2).plusMinutes(45).plusSeconds(7);
        String dbValue = converter.convertToDatabaseColumn(original);
        Duration fromDb = converter.convertToEntityAttribute(dbValue);
        assertThat(fromDb).isEqualTo(original);
    }

    @Test
    void jsonSerialize_deserializeEntity() throws Exception {
        TourEntity entity = TourEntity.builder()
                .id(10L)
                .name("Test Tour")
                .description("Desc")
                .fromLocation("Start")
                .toLocation("End")
                .transportType("bike")
                .distance(12.34)
                .estimatedTime(Duration.ofHours(1).plusMinutes(15))
                .routeImageUrl("http://example.com/map.png")
                .popularity(5)
                .childFriendliness(4.2)
                .build();

        String json = mapper.writeValueAsString(entity);
        assertThat(json)
                .contains("\"id\":10")
                .contains("\"name\":\"Test Tour\"")
                .contains("\"fromLocation\":\"Start\"")
                .contains("\"estimatedTime\":\"01:15:00\"")
                .contains("\"popularity\":5");

        TourEntity round = mapper.readValue(json, TourEntity.class);
        assertThat(round.getId()).isEqualTo(10L);
        assertThat(round.getEstimatedTime()).isEqualTo(Duration.ofHours(1).plusMinutes(15));
        assertThat(round.getChildFriendliness()).isEqualTo(4.2);
    }



}