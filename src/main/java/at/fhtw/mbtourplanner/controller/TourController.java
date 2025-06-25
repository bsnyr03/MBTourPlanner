package at.fhtw.mbtourplanner.controller;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.service.TourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/tours")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TourController {
    private final TourService tourService;

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



}