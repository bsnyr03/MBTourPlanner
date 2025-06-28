package at.fhtw.mbtourplanner;

import at.fhtw.mbtourplanner.repository.TourEntity;
import at.fhtw.mbtourplanner.repository.TourLogEntity;
import at.fhtw.mbtourplanner.repository.TourLogRepository;
import at.fhtw.mbtourplanner.repository.TourRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TourLogRepositoryTest {
    @Autowired
    private TourLogRepository tourLogRepository;
    @Autowired
    private TourRepository tourRepository;

    private TourEntity createValidTour(String name) {
        return tourRepository.save(
                TourEntity.builder()
                        .name(name)
                        .description("desc")
                        .fromLocation("A")
                        .toLocation("B")
                        .transportType("bike")
                        .distance(10.0)
                        .estimatedTime(Duration.ofMinutes(30))
                        .routeImageUrl("url")
                        .build()
        );
    }

    private TourLogEntity createValidLog(String comment, TourEntity tour) {
        return TourLogEntity.builder()
                .comment(comment)
                .difficulty(2)
                .logDateTime(LocalDateTime.now())
                .totalDistance(5.0)
                .totalTime(Duration.ofMinutes(30))
                .rating(5)
                .tour(tour)
                .build();
    }

    @Test
    void testSaveAndFindById() {
        TourEntity tour = createValidTour("Repo Test Tour");
        TourLogEntity log = createValidLog("Repo Test", tour);
        TourLogEntity saved = tourLogRepository.save(log);
        assertThat(tourLogRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void testFindAll() {
        TourEntity tour = createValidTour("A Tour");
        tourLogRepository.save(createValidLog("A", tour));
        assertThat(tourLogRepository.findAll()).isNotEmpty();
    }

    @Test
    void testDelete() {
        TourEntity tour = createValidTour("B Tour");
        TourLogEntity log = tourLogRepository.save(createValidLog("B", tour));
        tourLogRepository.delete(log);
        assertThat(tourLogRepository.findById(log.getId())).isNotPresent();
    }
}