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
        log.info("GET /api/tours called");
        return tourService.getAllTours();
    }

    @PostMapping
    public void addTour(@Valid @RequestBody Tour tour) throws SQLException {
        log.info("POST /api/tours called with tour={}", tour);
        tourService.addTour(tour);
    }

    @GetMapping("/{id}")
    public Tour getTourById(@PathVariable Long id) throws SQLException {
        log.info("GET /api/tours/{} called", id);
        return tourService.getTourById(id);
    }

    @PutMapping("/{id}")
    public Tour updateTour(@Valid @PathVariable Long id, @RequestBody Tour tour) throws SQLException {
        log.info("PUT /api/tours/{} called with tour={}", id, tour);
        return tourService.updateTour(id, tour);
    }

    @DeleteMapping("/{id}")
    public void deleteTour(@PathVariable Long id) throws SQLException {
        log.info("DELETE /api/tours/{} called", id);
        tourService.deleteTour(id);
    }

    @GetMapping("/search")
    public List<Tour> searchTours(@RequestParam String q) {
        log.info("Searching tours with query: {}", q);
        List<Tour> result = tourService.searchTours(q);
        log.debug("Search returned {} tours for query='{}'", result.size(), q);
        return result;
    }

    @GetMapping("/export")
    public ResponseEntity<List<Tour>> exportALlToursJSON() throws SQLException {
        log.info("GET /api/tours/export (JSON) called");
        List<Tour> tours = tourService.getAllTours();
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tours.json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(tours);
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Integer>> importAllToursJSON(@RequestBody List<Tour> tours) throws SQLException {
        log.info("POST /api/tours/import (JSON) called with {} tours", tours.size());
        for (Tour tour : tours) {
            tourService.addTour(tour);
        }
        return ResponseEntity.ok(Map.of("imported", tours.size(), "status", HttpStatus.OK.value()));
    }


    @GetMapping(value = "/export/csv", produces = "text/csv")
    public ResponseEntity<byte[]> exportAllToursCSV() throws SQLException {
        log.info("GET /api/tours/export/csv called");
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
        log.info("POST /api/tours/import/csv called with file={}", file.getOriginalFilename());
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

    @PostMapping("/{id}/image")
    public ResponseEntity<Void> uploadRouteImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        log.info("POST /api/tours/{}/image called with file={}", id, file.getOriginalFilename());
        try {
            tourService.storeRouteImage(id, file);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error uploading image for tour {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getRouteImage(@PathVariable Long id) {
        log.info("GET /api/tours/{}/image called", id);
        try {
            byte[] imageData = tourService.getRouteImage(id);
            if (imageData == null || imageData.length == 0) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        } catch (Exception e) {
            log.error("Error retrieving image for tour {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
