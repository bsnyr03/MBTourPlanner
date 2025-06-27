package at.fhtw.mbtourplanner.controller;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.service.TourService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/tours")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TourController {
    private final TourService tourService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public List<Tour> getAll() throws SQLException {
        return tourService.getAllTours();
    }

    @PostMapping
    public void addTour(@Valid @RequestBody Tour tour) throws SQLException {
        tourService.addTour(tour);
    }

    @GetMapping("/{id}")
    public Tour getTourById(@PathVariable Long id) throws SQLException {
        return tourService.getTourById(id);
    }

    @PutMapping("/{id}")
    public Tour updateTour(@Valid @PathVariable Long id, @RequestBody Tour tour) throws SQLException {
        return tourService.updateTour(id, tour);
    }

    @DeleteMapping("/{id}")
    public void deleteTour(@PathVariable Long id) throws SQLException {
        tourService.deleteTour(id);
    }

    @GetMapping("/search")
    public List<Tour> searchTours(@RequestParam String q) {
        log.info("Searching tours with query: {}", q);
        return tourService.searchTours(q);
    }

    @GetMapping("/export")
    public ResponseEntity<List<Tour>> exportALlToursJSON() throws SQLException {
        List<Tour> tours = tourService.getAllTours();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tours.json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(tours);
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Integer>> importAllToursJSON(@RequestBody List<Tour> tours) throws SQLException {
        for (Tour tour : tours) {
            tourService.addTour(tour);
        }
        return ResponseEntity.ok(Map.of("imported", tours.size(), "status", HttpStatus.OK.value()));
    }


    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportAllToursCSV() throws SQLException {
        List<Tour> tours = null;
        try {
            tours = tourService.getAllTours();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        StringBuilder sb = new StringBuilder();

        // CSV header
        sb.append("id,name, description, fromLocation, toLocation, transportType, distance, estimatedTime, routeImageURL, popularity, childFriendliness\n");

        // CSV rows
        for (Tour tour : tours) {
            sb.append(tour.getId()).append(",")
                    .append(tour.getName()).append(",")
                    .append(tour.getDescription()).append(",")
                    .append(tour.getFromLocation()).append(",")
                    .append(tour.getToLocation()).append(",")
                    .append(tour.getTransportType()).append(",")
                    .append(tour.getDistance()).append(",")
                    .append(tour.getEstimatedTime()).append(",")
                    .append(tour.getRouteImageUrl()).append(",")
                    .append(tour.getPopularity()).append(",")
                    .append(tour.getChildFriendliness())
                    .append("\n");
        }

        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tours.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);

    }





}
