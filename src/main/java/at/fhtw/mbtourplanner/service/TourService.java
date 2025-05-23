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

    public List<Tour> getAllTours() {
        return tourMapper.toDto(tourRepository.findAll());
    }

    public void addTour(Tour tour) throws SQLException {
        tourRepository.save(tourMapper.toEntity(tour));
    }

    public Tour getTourById(Long id) throws SQLException {
        TourEntity tourEntity = tourRepository.findById(id).orElseThrow(() -> new RuntimeException("Tour not found"));
        return tourMapper.toDto(tourEntity);
    }

    public Tour updateTour(Long id, Tour tour) throws SQLException {
        TourEntity existingTour = tourRepository.findById(id).orElseThrow(() -> new RuntimeException("Tour not found"));
        existingTour.setName(tour.getName());
        existingTour.setDescription(tour.getDescription());
        existingTour.setFromLocation(tour.getFromLocation());
        existingTour.setToLocation(tour.getToLocation());
        existingTour.setTransportType(tour.getTransportType());
        existingTour.setDistance(tour.getDistance());
        existingTour.setEstimatedTime(tour.getEstimatedTime());
        existingTour.setRouteImageUrl(tour.getRouteImageUrl());
        return tourMapper.toDto(tourRepository.save(existingTour));
    }

    public void deleteTour(Long id) throws SQLException {
        TourEntity tourEntity = tourRepository.findById(id).orElseThrow(() -> new RuntimeException("Tour not found"));
        tourRepository.delete(tourEntity);
    }





}