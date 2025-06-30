package at.fhtw.mbtourplanner.controller;

import at.fhtw.mbtourplanner.model.TourLog;
import at.fhtw.mbtourplanner.service.TourLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("api/tours/{tourId}/tour_logs")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TourLogController {

    private final TourLogService tourLogService;

    @GetMapping
    public List<TourLog> getAll(@PathVariable Long tourId) throws SQLException {
        log.info("GET /api/tours/{}/tour_logs called", tourId);
        List<TourLog> logs = tourLogService.getLogsForTour(tourId);
        log.debug("Returning {} logs for tourId={}", logs.size(), tourId);
        return logs;
    }

    @GetMapping("/{logId}")
    public TourLog getOneLog(@PathVariable Long tourId, @PathVariable("logId") Long id) throws SQLException {
        log.info("GET /api/tours/{}/tour_logs/{} called", tourId, id);
        TourLog logEntry = tourLogService.getLog(tourId, id);
        log.debug("Found log {} for tourId={}", logEntry.getId(), tourId);
        return logEntry;
    }

    @PostMapping
    public TourLog create(@PathVariable Long tourId, @Valid @RequestBody TourLog tourLog) throws SQLException {
        log.info("POST /api/tours/{}/tour_logs called with log={}", tourId, tourLog);
        TourLog created = tourLogService.addLog(tourId, tourLog);
        log.debug("Created log {} for tourId={}", created.getId(), tourId);
        return created;
    }

    @PutMapping("/{logId}")
    public TourLog update(@PathVariable Long tourId, @Valid @PathVariable("logId") Long id, @RequestBody TourLog tourLog) throws SQLException {
        log.info("PUT /api/tours/{}/tour_logs/{} called with log={}", tourId, id, tourLog);
        TourLog updated = tourLogService.updateLog(tourId, id, tourLog);
        log.debug("Updated log {} for tourId={}", updated.getId(), tourId);
        return updated;
    }

    @DeleteMapping("/{logId}")
    public void delete(@PathVariable Long tourId, @PathVariable("logId") Long id) throws SQLException {
        log.info("DELETE /api/tours/{}/tour_logs/{} called", tourId, id);
        tourLogService.deleteLog(tourId, id);
        log.debug("Deleted log {} for tourId={}", id, tourId);
    }

    @GetMapping("/search")
    public List<TourLog> search(@PathVariable Long tourId, @RequestParam String q) throws SQLException {
        log.info("GET /api/tours/{}/tour_logs/search called with query={}", tourId, q);
        List<TourLog> results = tourLogService.searchLogs(tourId, q);
        log.debug("Search returned {} logs for tourId={} and query='{}'", results.size(), tourId, q);
        return results;
    }


}
