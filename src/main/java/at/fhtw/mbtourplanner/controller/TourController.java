package at.fhtw.mbtourplanner.controller;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.repository.TourEntity;
import at.fhtw.mbtourplanner.service.TourMapper;
import at.fhtw.mbtourplanner.service.TourService;
import com.opencsv.CSVReader;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/tours")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TourController {
    private final TourService tourService;
    private final TourMapper tourMapper;

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

    @PostMapping(value = "/import/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Integer>> importAllToursCSV(@RequestParam ("file") MultipartFile file) throws Exception {
        List<TourEntity> toImport = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            reader.readNext();
            String[] line;
            while ((line = reader.readNext()) != null) {
                TourEntity tour = new TourEntity();
                tour.setName(line[1]);
                tour.setDescription(line[2]);
                tour.setFromLocation(line[3]);
                tour.setToLocation(line[4]);
                tour.setTransportType(line[5]);
                tour.setDistance(Double.parseDouble(line[6]));
                tour.setEstimatedTime(Duration.parse(line[7]));
                tour.setRouteImageUrl(line[8]);
                tour.setPopularity(Integer.parseInt(line[9]));
                tour.setChildFriendliness(Double.parseDouble(line[10]));
                toImport.add(tour);
            }

        } catch (Exception e) {
            log.error("Error reading CSV file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("imported", 0, "status", HttpStatus.BAD_REQUEST.value()));
        }

        for (TourEntity tourEntity : toImport) {
            tourService.addTour(tourMapper.toDto(tourEntity));
        }

        return ResponseEntity.ok(Map.of("imported", toImport.size(), "status", HttpStatus.OK.value()));
    }






}
