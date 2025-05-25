package at.fhtw.mbtourplanner.service;


import at.fhtw.mbtourplanner.model.TourLog;
import at.fhtw.mbtourplanner.repository.TourEntity;
import at.fhtw.mbtourplanner.repository.TourLogEntity;
import at.fhtw.mbtourplanner.repository.TourLogRepository;
import at.fhtw.mbtourplanner.repository.TourRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TourLogService {

    private final TourLogRepository tourLogRepository;
    private final TourRepository tourRepository;
    private final TourLogMapper mapper;

    public List<TourLog> getLogsForTour(Long tourId) throws SQLException {
        TourEntity tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        List<TourLogEntity> ents = tourLogRepository.findAllByTour(tour);
        ents.forEach(e -> e.setTour(tour));
        return mapper.toDto(ents);
    }

    public TourLog getLog(Long tourId, Long logId) throws SQLException{
        TourLogEntity tourLogEntity = tourLogRepository.findById(logId).orElseThrow(() -> new RuntimeException("TourLog not found"));
        if(!tourLogEntity.getTour().getId().equals(tourId)) {
            throw new RuntimeException("TourLog does not belong to the specified Tour");
        }
        return mapper.toDto(tourLogEntity);
    }

    public TourLog addLog(Long tourId, TourLog tourLog) throws SQLException {
        TourEntity tour = tourRepository.findById(tourId).orElseThrow(() -> new RuntimeException("Tour not found"));
        TourLogEntity tourLogEntity = mapper.toEntity(tourLog);
        tourLogEntity.setTour(tour);
        tourLogRepository.save(tourLogEntity);
        return mapper.toDto(tourLogEntity);
    }

    public TourLog updateLog(Long tourId, Long logId, TourLog dto) throws SQLException {
        TourLogEntity existing = tourLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Log not found"));
        if (!existing.getTour().getId().equals(tourId))
            throw new RuntimeException("Log does not belong to tour");
        existing.setLogDateTime(dto.getLogDateTime());
        existing.setComment(dto.getComment());
        existing.setDifficulty(dto.getDifficulty());
        existing.setTotalDistance(dto.getTotalDistance());
        existing.setTotalTime(dto.getTotalTime());
        existing.setRating(dto.getRating());
        TourLogEntity saved = tourLogRepository.save(existing);
        return mapper.toDto(saved);
    }

    public void deleteLog(Long tourId, Long logId) throws SQLException {
        TourLogEntity tourLogEntity = tourLogRepository.findById(logId).orElseThrow(() -> new RuntimeException("TourLog not found"));
        if (!tourLogEntity.getTour().getId().equals(tourId)) {
            throw new RuntimeException("TourLog does not belong to the specified Tour");
        }
        tourLogRepository.delete(tourLogEntity);
    }



}
