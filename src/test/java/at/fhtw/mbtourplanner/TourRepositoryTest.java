package at.fhtw.mbtourplanner;

import at.fhtw.mbtourplanner.repository.TourEntity;
import at.fhtw.mbtourplanner.repository.TourRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TourRepositoryTest {
    @Autowired private TourRepository tourRepository;

    private TourEntity createValidTour(String name) {
        return TourEntity.builder()
                .name(name)
                .description("desc")
                .fromLocation("A")
                .toLocation("B")
                .transportType("bike")
                .distance(10.0)
                .estimatedTime("1:00:00")
                .routeImageUrl("url")
                .build();
    }

    @Test void testSaveAndFindById() {
        TourEntity tour = createValidTour("Repo Test");
        TourEntity saved = tourRepository.save(tour);
        assertThat(tourRepository.findById(saved.getId())).isPresent();
    }
    @Test void testFindAll() {
        tourRepository.save(createValidTour("A"));
        assertThat(tourRepository.findAll()).isNotEmpty();
    }
    @Test void testDelete() {
        TourEntity tour = tourRepository.save(createValidTour("B"));
        tourRepository.delete(tour);
        assertThat(tourRepository.findById(tour.getId())).isNotPresent();
    }
}