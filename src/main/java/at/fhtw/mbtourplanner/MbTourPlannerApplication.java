package at.fhtw.mbtourplanner;

import at.fhtw.mbtourplanner.repository.TourEntity;
import at.fhtw.mbtourplanner.repository.TourLogEntity;
import at.fhtw.mbtourplanner.repository.TourLogRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import at.fhtw.mbtourplanner.repository.TourRepository;
import org.postgresql.util.PGInterval;

import java.sql.SQLException;
import java.time.LocalDateTime;

@SpringBootApplication
@RequiredArgsConstructor
public class MbTourPlannerApplication {

    @Autowired
    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;

    public static void main(String[] args) {
        SpringApplication.run(MbTourPlannerApplication.class, args);
    }

    /*
    @PostConstruct
    void initDB() throws SQLException {
        tourRepository.save(
                TourEntity.builder()
                        .name("Kadikoy Tour")
                        .description("A guided bike tour through the historic city center.")
                        .fromLocation("Old Town")
                        .toLocation("City Park")
                        .transportType("bike")
                        .distance(12.5)
                        .estimatedTime("1:30:00")
                        .routeImageUrl("https://example.com/city-tour.png")
                        .popularity(3)
                        .childFriendliness(4.5)

                        .build()
        );
        tourRepository.save(
                TourEntity.builder()
                        .name("Barcelona City Tour")
                        .description("A challenging hike up the local mountain trail.")
                        .fromLocation("Trailhead")
                        .toLocation("Summit")
                        .transportType("hike")
                        .distance(8.0)
                        .estimatedTime("3:00:00")
                        .routeImageUrl("https://example.com/mountain-hike.png")
                        .popularity(5)
                        .childFriendliness(2.0)
                        .build()

        );
        tourRepository.save(
                TourEntity.builder()
                        .name("Istanbul City Tour")
                        .description("A relaxing walk along the beach promenade.")
                        .fromLocation("Beach Entrance")
                        .toLocation("Beach Cafe")
                        .transportType("walk")
                        .distance(5.0)
                        .estimatedTime("1:00:00")
                        .routeImageUrl("https://example.com/beach-vacation.png")
                        .popularity(4)
                        .childFriendliness(5.0)
                        .build()
        );
        tourLogRepository.save(
                TourLogEntity.builder()
                        .tour(tourRepository.findById(1L).orElse(null))
                        .logDateTime(LocalDateTime.now())
                        .comment("Great tour, enjoyed the sights!")
                        .difficulty(3)
                        .totalDistance(12.5)
                        .totalTime("1:30:00")
                        .rating("5 stars")
                        .build()
        );

        tourLogRepository.save(
                TourLogEntity.builder()
                        .tour(tourRepository.findById(2L).orElse(null))
                        .logDateTime(LocalDateTime.now())
                        .comment("Challenging but rewarding hike!")
                        .difficulty(4)
                        .totalDistance(8.0)
                        .totalTime("3:00:00")
                        .rating("4 stars")
                        .build()
        );

        tourLogRepository.save(
                TourLogEntity.builder()
                        .tour(tourRepository.findById(3L).orElse(null))
                        .logDateTime(LocalDateTime.now())
                        .comment("Relaxing walk, perfect for a sunny day.")
                        .difficulty(2)
                        .totalDistance(5.0)
                        .totalTime("1:00:00")
                        .rating("5 stars")
                        .build()
        );

    }
     */

}


