package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.repository.TourEntity;
import at.fhtw.mbtourplanner.repository.TourLogEntity;
import at.fhtw.mbtourplanner.repository.TourLogRepository;
import at.fhtw.mbtourplanner.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourService {
    private final TourRepository tourRepository;
    private final TourMapper tourMapper;
    private final TourLogRepository tourLogRepository;

    public List<Tour> getAllTours() throws SQLException {
        List<TourEntity> tourEntities = tourRepository.findAll();
        List<Tour> dtos = new ArrayList<>();

        for (TourEntity tourEntity : tourEntities) {

            Tour dto = tourMapper.toDto(tourEntity);

            int popularity = tourLogRepository.countByTourId(tourEntity.getId());
            dto.setPopularity(popularity);

            double childFriendliness = computeChildFriendliness(tourEntity);
            dto.setChildFriendliness(childFriendliness);

            dtos.add(dto);
        }
        return dtos;
    }

    public List<Tour> searchTours(String q) {
        List<TourEntity> tourEntities = tourRepository.searchTours(q);
        return tourMapper.toDto(tourEntities);
    }

    public void addTour(Tour tour) throws SQLException {
        tourRepository.save(tourMapper.toEntity(tour));
    }

    public Tour getTourById(Long id) throws SQLException {
        TourEntity tourEntity = tourRepository.findById(id).orElseThrow(() -> new RuntimeException("Tour not found"));
        Tour dto = tourMapper.toDto(tourEntity);

        dto.setPopularity(tourLogRepository.countByTourId(id));
        dto.setChildFriendliness(computeChildFriendliness(tourEntity));

        return dto;
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
        existingTour.setPopularity(tour.getPopularity());
        existingTour.setChildFriendliness(tour.getChildFriendliness());
        return tourMapper.toDto(tourRepository.save(existingTour));
    }

    public void deleteTour(Long id) throws SQLException {
        TourEntity tourEntity = tourRepository.findById(id).orElseThrow(() -> new RuntimeException("Tour not found"));
        tourRepository.delete(tourEntity);
    }

    // Berechnet einen einfachen ChildFrindliness-Wert aus Difficulty und Zeit und Distance
    private double computeChildFriendliness(TourEntity tourEntity) {
        List<TourLogEntity> logs = tourLogRepository.findAllByTour(tourEntity);
        if(logs.isEmpty()){
            return 0.0;
        }

        // Durchschnittlicher Schwierigkeitsgrad
        double averageDifference = logs.stream().mapToInt(TourLogEntity::getDifficulty).average().orElse(0);


        // Durchschnittliche Zeit in Sekunden
        double averageTime = logs.stream().mapToDouble(TourLogEntity::getTotalDistance).average().orElse(0);

        // Durchschnittliche Bewertung: je niedriger Difficulty, desto höher die Bewertung
        return (6- averageDifference) + (1000-averageDifference) / 200 + (3600 - averageTime) / 600;

    }

    // Parset eine Uhrzeit im Format "HH:MM:SS" oder "MM:SS" oder "SS" in Sekunden
    private long parseTimeToSeconds(String hms) {
        String[] parts = hms.split(":");
        long seconds = 0;
        if (parts.length == 3) {
            seconds += Long.parseLong(parts[0]) * 3600; // Stunden
            seconds += Long.parseLong(parts[1]) * 60;   // Minuten
            seconds += Long.parseLong(parts[2]);        // Sekunden
        } else if (parts.length == 2) {
            seconds += Long.parseLong(parts[0]) * 60;   // Minuten
            seconds += Long.parseLong(parts[1]);         // Sekunden
        } else if (parts.length == 1) {
            seconds += Long.parseLong(parts[0]);         // Sekunden
        }
        return seconds;
    }
}