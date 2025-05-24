package at.fhtw.mbtourplanner.repository;


import at.fhtw.mbtourplanner.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourLogRepository extends JpaRepository<TourLogEntity, Long> {
    List<TourLogEntity> findAllByTour(TourEntity tour);
}
