package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.repository.TourEntity;
import at.fhtw.mbtourplanner.repository.TourRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

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
            List<List<Double>> coords = (List<List<Double>>) routeInfo.get("route");
            if (coords == null || coords.isEmpty()) {
                throw new RuntimeException("No route coordinates found in route info");
            }
            String staticMapUrlFallback = openRouteService.buildStaticMapUrl(coords, 600, 400, 14);
            entity.setRouteImageUrl(staticMapUrlFallback);
            entity.setDistance(distance != null ? distance.doubleValue() / 1000.0 : 0.0);
            entity.setEstimatedTime(duration != null ? Duration.ofSeconds(duration.longValue()) : Duration.ZERO);
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
            List<List<Double>> coords = (List<List<Double>>) routeInfo.get("route");
            if (coords == null || coords.isEmpty()) {
                throw new RuntimeException("No route coordinates found in route info");
            }
            String staticMapUrlFallback = openRouteService.buildStaticMapUrl(coords, 600, 400, 14);
            existing.setRouteImageUrl(staticMapUrlFallback);
            existing.setDistance(distance != null ? distance.doubleValue() / 1000.0 : 0.0);
            existing.setEstimatedTime(duration != null ? Duration.ofSeconds(duration.longValue()) : Duration.ZERO);
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

    @Transactional
    public void storeRouteImage(Long tourId, MultipartFile file) {
        TourEntity ent = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        try {
            ent.setRouteImageData(file.getBytes());
            tourRepository.save(ent);
        } catch (IOException e) {
            throw new RuntimeException("Image storage failed", e);
        }
    }

    public byte[] getRouteImage(Long tourId) {
        TourEntity ent = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        byte[] imageData = ent.getRouteImageData();
        if (imageData == null || imageData.length == 0) {
            throw new RuntimeException("No image data found for tour");
        }
        return imageData;
    }

}