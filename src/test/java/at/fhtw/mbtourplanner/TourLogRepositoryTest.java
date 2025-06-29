package at.fhtw.mbtourplanner.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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
                        .popularity(0)
                        .childFriendliness(0.0)
                        .build()
        );
    }

    private TourLogEntity createValidLog(String comment, TourEntity tour) {
        return TourLogEntity.builder()
                .tour(tour)
                .logDateTime(LocalDateTime.now())
                .comment(comment)
                .difficulty(2)
                .totalDistance(5.0)
                .totalTime(Duration.ofMinutes(30))
                .rating(5)
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

    @Test
    void countByTourId_returnsZeroInitially() {
        TourEntity tour = createValidTour("Count Tour");
        long count = tourLogRepository.countByTourId(tour.getId());
        assertThat(count).isZero();
    }

    @Test
    void countByTourId_returnsCorrectCountAfterInsert() {
        TourEntity tour = createValidTour("Count Tour 2");
        tourLogRepository.save(createValidLog("X", tour));
        long count = tourLogRepository.countByTourId(tour.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    void findAllByTour_returnsAllLogsForTour() {
        TourEntity tour = createValidTour("List Tour");
        TourLogEntity log1 = tourLogRepository.save(createValidLog("First", tour));
        TourLogEntity log2 = tourLogRepository.save(createValidLog("Second", tour));
        List<TourLogEntity> logs = tourLogRepository.findAllByTour(tour);
        assertThat(logs)
                .hasSize(2)
                .extracting(TourLogEntity::getComment)
                .containsExactlyInAnyOrder("First", "Second");
    }

    @Test
    void searchLogs_findsByCommentIgnoringCase() {
        TourEntity tour = createValidTour("Search Tour");
        tourLogRepository.save(createValidLog("FindMe", tour));
        tourLogRepository.save(createValidLog("Other", tour));
        List<TourLogEntity> found = tourLogRepository.searchLogs(tour.getId(), "findme");
        assertThat(found)
                .hasSize(1)
                .first()
                .extracting(TourLogEntity::getComment)
                .isEqualTo("FindMe");
    }
}