package at.fhtw.mbtourplanner.controller;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.service.TourService;
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
    public List<Tour> getAll() {
        return tourService.getAllTours();
    }

    @PostMapping
    public void addTour(@RequestBody Tour tour) throws SQLException {
        tourService.addTour(tour);
    }
}