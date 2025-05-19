package at.fhtw.mbtourplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TourRepository extends JpaRepository<TourEntity, Long> {
}