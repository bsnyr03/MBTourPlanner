package at.fhtw.mbtourplanner.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TourRepositoryTest {

    @Autowired
    private TourRepository tourRepository;

    private TourEntity tourA;
    private TourEntity tourB;

    private TourEntity createValidTour(String name, Duration estimatedTime) {
        return TourEntity.builder()
                .name(name)
                .description(name + " description")
                .fromLocation("Loc1")
                .toLocation("Loc2")
                .transportType("bike")
                .distance(10.0)
                .estimatedTime(estimatedTime)
                .routeImageUrl("http://example.com/" + name)
                .popularity(5)
                .childFriendliness(2.5)
                .build();
    }

    @BeforeEach
    void setUp() {
        tourA = tourRepository.save(createValidTour("Alpha Tour", Duration.ofHours(1)));
        tourB = tourRepository.save(createValidTour("Beta Tour", Duration.ofHours(2)));
    }

    @Test
    void testSaveAndFindById() {
        assertThat(tourRepository.findById(tourA.getId())).isPresent();
    }

    @Test
    void testFindAll() {
        List<TourEntity> all = tourRepository.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2)
                .extracting(TourEntity::getName)
                .contains(tourA.getName(), tourB.getName());
    }

    @Test
    void testDelete() {
        tourRepository.delete(tourA);
        assertThat(tourRepository.findById(tourA.getId())).isNotPresent();
    }

    @Test
    void searchTours_byName() {
        List<TourEntity> found = tourRepository.searchTours("alpha");
        assertThat(found).hasSize(1)
                .first()
                .extracting(TourEntity::getName)
                .isEqualTo("Alpha Tour");
    }

    @Test
    void searchTours_byDescription() {
        List<TourEntity> found = tourRepository.searchTours("beta");
        assertThat(found).hasSize(1)
                .first()
                .extracting(TourEntity::getDescription)
                .isEqualTo("Beta Tour description");
    }

    @Test
    void searchTours_byDistance() {
        List<TourEntity> found = tourRepository.searchTours("10.0");
        assertThat(found).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void searchTours_byEstimatedTime() {
        List<TourEntity> found1 = tourRepository.searchTours("01:00:00");
        assertThat(found1).hasSize(1)
                .first()
                .extracting(TourEntity::getEstimatedTime)
                .isEqualTo(Duration.ofHours(1));

        List<TourEntity> found2 = tourRepository.searchTours("02:00:00");
        assertThat(found2).hasSize(1)
                .first()
                .extracting(TourEntity::getEstimatedTime)
                .isEqualTo(Duration.ofHours(2));
    }

    @Test
    void searchTours_byPopularity() {
        List<TourEntity> found = tourRepository.searchTours("5");
        assertThat(found).hasSizeGreaterThanOrEqualTo(2)
                .allMatch(t -> t.getPopularity() == 5);
    }

    @Test
    void searchTours_byChildFriendliness() {
        List<TourEntity> found = tourRepository.searchTours("2.5");
        assertThat(found).hasSizeGreaterThanOrEqualTo(2)
                .allMatch(t -> t.getChildFriendliness() == 2.5);
    }

    @Test
    void searchTours_byRouteImageUrl() {
        List<TourEntity> found = tourRepository.searchTours("example.com/Alpha");
        assertThat(found).hasSize(1)
                .first()
                .extracting(TourEntity::getRouteImageUrl);
    }
}