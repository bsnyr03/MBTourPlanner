import at.fhtw.mbtourplanner.repository.TourLogEntity;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TourLogEntityTest {
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
}