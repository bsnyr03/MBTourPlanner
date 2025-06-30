package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourService {
    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final GeocodingService geocodingService;
    private final OpenRouteService openRouteService;

    public List<Tour> getAllTours() throws SQLException {
        log.info("Fetching all tours");
        var entities = tourRepository.findAll();
        var tours = tourMapper.toDto(entities);
        log.debug("Returning {} tours", tours.size());
        return tours;
    }

    public List<Tour> searchTours(String q) {
        log.info("Searching tours with query={}", q);
        var entities = tourRepository.searchTours(q);
        log.debug("Found {} tours for query='{}'", entities.size(), q);
        return tourMapper.toDto(entities);
    }

    public void addTour(Tour tour) throws SQLException {
        log.info("Adding new tour: {}", tour.getName());
        var entity = tourMapper.toEntity(tour);

        double[] from = geocodingService.geocode(entity.getFromLocation());
        double[] to   = geocodingService.geocode(entity.getToLocation());
        entity.setFromLat(from[0]);
        entity.setFromLon(from[1]);
        entity.setToLat(to[0]);
        entity.setToLon(to[1]);

        var routeInfo = openRouteService.getRouteInfo(
            "foot-walking",
            List.of(
                List.of(entity.getFromLon(), entity.getFromLat()),
                List.of(entity.getToLon(),   entity.getToLat())
            )
        );
        log.error("Route info: {}", routeInfo);
        String staticMapUrl = (String) routeInfo.get("staticMapUrl");
        Number distance = (Number) routeInfo.get("distance");
        Number duration = (Number) routeInfo.get("duration");

        if (staticMapUrl != null && distance != null && duration != null) {
            entity.setRouteImageUrl(staticMapUrl);
            entity.setDistance(distance.doubleValue() / 1000.0);
            entity.setEstimatedTime(Duration.ofSeconds(duration.longValue()));
        } else {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> features = (List<Map<String, Object>>) routeInfo.get("features");
            if (features == null || features.isEmpty()) {
                throw new RuntimeException("No features found in route info");
            }
            Map<String, Object> feature = features.getFirst();
            @SuppressWarnings("unchecked")
            Map<String, Object> props = (Map<String, Object>) feature.get("properties");
            @SuppressWarnings("unchecked")
            Map<String, Object> summary = (Map<String, Object>) props.get("summary");
            double rawDistance = ((Number) summary.get("distance")).doubleValue();
            long rawDurationSec = ((Number) summary.get("duration")).longValue();
            entity.setDistance(rawDistance / 1000.0);
            entity.setEstimatedTime(Duration.ofSeconds(rawDurationSec));

            @SuppressWarnings("unchecked")
            Map<String, Object> geometry = (Map<String, Object>) feature.get("geometry");
            @SuppressWarnings("unchecked")
            List<List<Double>> coords = (List<List<Double>>) geometry.get("coordinates");
            entity.setRouteImageUrl(
                    openRouteService.getStaticRouteMapUrl(
                            coords,
                            600,
                            400,
                            14
                    )
            );
        }

        tourRepository.save(entity);
        log.debug("Saved enriched tour id={} distance={} km time={}", entity.getId(), entity.getDistance(), entity.getEstimatedTime());
    }

    public Tour getTourById(Long id) throws SQLException {
        log.info("Fetching tour with id={}", id);
        var entity = tourRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tour not found"));
        var dto = tourMapper.toDto(entity);
        log.debug("Fetched tour: {}", dto);
        return dto;
    }

    public Tour updateTour(Long id, Tour tour) throws SQLException {
        log.info("Updating tour id={} with data={}", id, tour.getName());
        var existing = tourRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tour not found"));
        existing.setName(tour.getName());
        existing.setDescription(tour.getDescription());
        existing.setFromLocation(tour.getFromLocation());
        existing.setToLocation(tour.getToLocation());
        existing.setTransportType(tour.getTransportType());
        existing.setDistance(tour.getDistance());
        existing.setEstimatedTime(tour.getEstimatedTime());
        existing.setRouteImageUrl(tour.getRouteImageUrl());
        existing.setPopularity(tour.getPopularity());
        existing.setChildFriendliness(tour.getChildFriendliness());

        double[] from = geocodingService.geocode(existing.getFromLocation());
        double[] to   = geocodingService.geocode(existing.getToLocation());
        existing.setFromLat(from[0]);
        existing.setFromLon(from[1]);
        existing.setToLat(to[0]);
        existing.setToLon(to[1]);

        var routeInfo = openRouteService.getRouteInfo(
            "foot-walking",
            List.of(
                List.of(existing.getFromLon(), existing.getFromLat()),
                List.of(existing.getToLon(),   existing.getToLat())
            )
        );

        String staticMapUrl = (String) routeInfo.get("staticMapUrl");
        Number distance = (Number) routeInfo.get("distance");
        Number duration = (Number) routeInfo.get("duration");

        if (staticMapUrl != null && distance != null && duration != null) {
            existing.setRouteImageUrl(staticMapUrl);
            existing.setDistance(distance.doubleValue() / 1000.0);
            existing.setEstimatedTime(Duration.ofSeconds(duration.longValue()));
        } else {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> features = (List<Map<String, Object>>) routeInfo.get("features");
            if (features == null || features.isEmpty()) {
                throw new RuntimeException("No features found in route info");
            }
            Map<String, Object> feature = features.getFirst();
            @SuppressWarnings("unchecked")
            Map<String, Object> props = (Map<String, Object>) feature.get("properties");
            @SuppressWarnings("unchecked")
            Map<String, Object> summary = (Map<String, Object>) props.get("summary");
            double rawDistance = ((Number) summary.get("distance")).doubleValue();
            long rawDurationSec = ((Number) summary.get("duration")).longValue();
            existing.setDistance(rawDistance / 1000.0);
            existing.setEstimatedTime(Duration.ofSeconds(rawDurationSec));

            @SuppressWarnings("unchecked")
            Map<String, Object> geometry = (Map<String, Object>) feature.get("geometry");
            @SuppressWarnings("unchecked")
            List<List<Double>> coords = (List<List<Double>>) geometry.get("coordinates");
            existing.setRouteImageUrl(
                    openRouteService.getStaticRouteMapUrl(
                            coords,
                            600,
                            400,
                            14
                    )
            );
        }

        var saved = tourRepository.save(existing);
        var dto = tourMapper.toDto(saved);
        log.debug("Updated tour: {}", dto);
        return dto;
    }

    public void deleteTour(Long id) throws SQLException {
        log.info("Deleting tour with id={}", id);
        var entity = tourRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tour not found"));
        tourRepository.delete(entity);
        log.debug("Deleted tour with id={}", id);
    }
}