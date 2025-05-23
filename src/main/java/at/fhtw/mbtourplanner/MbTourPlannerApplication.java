package at.fhtw.mbtourplanner;

import at.fhtw.mbtourplanner.repository.TourEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import at.fhtw.mbtourplanner.repository.TourRepository;
import org.postgresql.util.PGInterval;

import java.sql.SQLException;

@SpringBootApplication
@RequiredArgsConstructor
public class MbTourPlannerApplication {

	@Autowired
	private TourRepository tourRepository;

	public static void main(String[] args) {
		SpringApplication.run(MbTourPlannerApplication.class, args);
	}

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
	            .build()
	    );
	}

}


