import at.fhtw.mbtourplanner.repository.TourEntity;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TourEntityTest {

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
}