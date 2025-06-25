package at.fhtw.mbtourplanner.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TourLogRepository extends JpaRepository<TourLogEntity, Long> {

    int countByTourId(long tourId);

    @Query("""
     SELECT tl from TourLogEntity tl
          WHERE tl.tour.id = :tourId
               AND(
                    LOWER(tl.comment) LIKE LOWER(CONCAT('%', :q, '%'))
                    OR LOWER(tl.totalTime) LIKE LOWER(CONCAT('%', :q, '%'))
                    )
     """)

    List<TourLogEntity> searchLogs(@Param("tourId") Long tourId, @Param("q") String query);
    List<TourLogEntity> findAllByTour(TourEntity tour);
}
