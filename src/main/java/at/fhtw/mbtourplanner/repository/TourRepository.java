package at.fhtw.mbtourplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TourRepository extends JpaRepository<TourEntity, Long> {

    @Query("""
            SELECT t FROM TourEntity t
            WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(t.fromLocation) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(t.toLocation) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(t.transportType) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(t.routeImageUrl) LIKE LOWER(CONCAT('%', :q, '%'))        
""")
    List<TourEntity> searchTours(@Param("q") String query);

}