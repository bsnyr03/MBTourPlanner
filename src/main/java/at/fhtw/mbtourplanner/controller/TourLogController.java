package at.fhtw.mbtourplanner.controller;

import at.fhtw.mbtourplanner.model.TourLog;
import at.fhtw.mbtourplanner.service.TourLogService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.NamedStoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("api/tours/{tourId}/tour_logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TourLogController {

    private final TourLogService tourLogService;

    @GetMapping
    public List<TourLog> getAll(@PathVariable Long tourId) throws SQLException {
        return tourLogService.getLogsForTour(tourId);
    }

    @GetMapping("/{logId}")
    public TourLog getOneLog(@PathVariable Long tourId, @PathVariable("logId") Long id) throws SQLException {
        return tourLogService.getLog(tourId, id);
    }

    @PostMapping
    public TourLog create(@PathVariable Long tourId, @RequestBody TourLog tourLog) throws SQLException {
        return tourLogService.addLog(tourId, tourLog);
    }

    @PutMapping("/{logId}")
    public TourLog update(@PathVariable Long tourId, @PathVariable("logId") Long id, @RequestBody TourLog tourLog) throws SQLException {
        return tourLogService.updateLog(tourId, id, tourLog);
    }






}
