package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.Tour;
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

}