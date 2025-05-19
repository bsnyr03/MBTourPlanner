package at.fhtw.mbtourplanner;

import at.fhtw.mbtourplanner.repository.TourEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import at.fhtw.mbtourplanner.repository.TourRepository;
import java.time.Duration;

@SpringBootApplication
@RequiredArgsConstructor
public class MbTourPlannerApplication {

	@Autowired
	private TourRepository tourRepository;

	public static void main(String[] args) {
		SpringApplication.run(MbTourPlannerApplication.class, args);
	}

	@PostConstruct
	void initDB() {
	    tourRepository.save(
	        TourEntity.builder()
	            .name("City Highlights")
	            .description("A guided bike tour through the historic city center.")
	            .fromLocation("Old Town")
	            .toLocation("City Park")
	            .transportType("bike")
	            .distance(12.5)
	            .estimatedTime(Duration.ofHours(1).plusMinutes(30))
	            .routeImageUrl("https://example.com/city-tour.png")
	            .build()
	    );
	    tourRepository.save(
	        TourEntity.builder()
	            .name("Mountain Hike")
	            .description("A challenging hike up the local mountain trail.")
	            .fromLocation("Trailhead")
	            .toLocation("Summit")
	            .transportType("hike")
	            .distance(8.0)
	            .estimatedTime(Duration.ofHours(4))
	            .routeImageUrl("https://example.com/mountain-hike.png")
	            .build()
	    );
	    tourRepository.save(
	        TourEntity.builder()
	            .name("Beach Vacation")
	            .description("A relaxing walk along the coastal boardwalk.")
	            .fromLocation("Pier")
	            .toLocation("Lighthouse")
	            .transportType("walk")
	            .distance(5.0)
	            .estimatedTime(Duration.ofHours(2))
	            .routeImageUrl("https://example.com/beach-vacation.png")
	            .build()
	    );
	}

}


