package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.repository.TourEntity;
import at.fhtw.mbtourplanner.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourService {
    private final TourRepository tourRepository;
    private final TourMapper tourMapper;

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
        tourRepository.save(entity);
        log.debug("Saved tour with id={}", entity.getId());
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