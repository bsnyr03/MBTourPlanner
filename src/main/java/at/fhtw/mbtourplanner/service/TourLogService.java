package at.fhtw.mbtourplanner.service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TourLogService {

    private final TourLogRepository tourLogRepository;
    private final TourRepository tourRepository;
    private final TourLogMapper mapper;

    public List<TourLog> getLogsForTour(Long tourId) throws SQLException {
        log.info("Fetching all logs for tourId={}", tourId);
        TourEntity tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        List<TourLogEntity> ents = tourLogRepository.findAllByTour(tour);
        log.debug("Found {} TourLogEntity entries for tourId={}", ents.size(), tourId);
        ents.forEach(e -> e.setTour(tour));
        return mapper.toDto(ents);
    }

    public List<TourLog> searchLogs(Long tourId, String q) throws SQLException {
        log.info("Searching logs for tourId={} with query='{}'", tourId, q);
        List<TourLogEntity> ents = tourLogRepository.searchLogs(tourId, q);
        log.debug("Search returned {} entries for tourId={} and query='{}'", ents.size(), tourId, q);
        return mapper.toDto(ents);
    }

    public TourLog getLog(Long tourId, Long logId) throws SQLException{
        log.info("Fetching TourLog with id={} for tourId={}", logId, tourId);
        TourLogEntity tourLogEntity = tourLogRepository.findById(logId).orElseThrow(() -> new RuntimeException("TourLog not found"));
        log.debug("Fetched TourLogEntity: {}", tourLogEntity);
        if(!tourLogEntity.getTour().getId().equals(tourId)) {
            throw new RuntimeException("TourLog does not belong to the specified Tour");
        }
        return mapper.toDto(tourLogEntity);
    }

    public TourLog addLog(Long tourId, TourLog tourLog) throws SQLException {
        log.info("Adding TourLog for tourId={} with data={}", tourId, tourLog);
        TourEntity tour = tourRepository.findById(tourId).orElseThrow(() -> new RuntimeException("Tour not found"));
        TourLogEntity tourLogEntity = mapper.toEntity(tourLog);
        tourLogEntity.setTour(tour);
        tourLogRepository.save(tourLogEntity);
        log.debug("Saved TourLogEntity with id={} for tourId={}", tourLogEntity.getId(), tourId);

        validateTourLog(tourLogEntity);
        updateTourStats(tour, tourId);

        return mapper.toDto(tourLogEntity);
    }

    public TourLog updateLog(Long tourId, Long logId, TourLog dto) throws SQLException {
        log.info("Updating TourLog id={} for tourId={} with data={}", logId, tourId, dto);
        TourEntity tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
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
        log.debug("Updated TourLogEntity: {}", saved);

        validateTourLog(existing);
        updateTourStats(tour, tourId);

        return mapper.toDto(saved);
    }

    public void deleteLog(Long tourId, Long logId) throws SQLException {
        log.info("Deleting TourLog id={} for tourId={}", logId, tourId);
        TourEntity tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));
        TourLogEntity tourLogEntity = tourLogRepository.findById(logId).orElseThrow(() -> new RuntimeException("TourLog not found"));
        if (!tourLogEntity.getTour().getId().equals(tourId)) {
            throw new RuntimeException("TourLog does not belong to the specified Tour");
        }

        updateTourStats(tour, tourId);

        tourLogRepository.delete(tourLogEntity);
        log.debug("Deleted TourLogEntity id={} for tourId={}", logId, tourId);
    }

    public void updateTourStats(TourEntity tour, Long tourId) throws SQLException {
        log.info("Updating stats for tourId={}", tourId);
        int popularity = tourLogRepository.countByTourId(tour.getId());
        double childFriendliness = computeChildFriendliness(tour);
        log.debug("Computed stats popularity={} and childFriendliness={} for tourId={}", popularity, childFriendliness, tourId);
        tour.setPopularity(popularity);
        tour.setChildFriendliness(childFriendliness);
        tourRepository.save(tour);
    }

    // Berechnet einen einfachen ChildFrindliness-Wert aus Difficulty und Zeit und Distance
    private double computeChildFriendliness(TourEntity tourEntity) {
        List<TourLogEntity> logs = tourLogRepository.findAllByTour(tourEntity);
        if(logs.isEmpty()){
            return 0.0;
        }

        // Durchschnittlicher Schwierigkeitsgrad
        double averageDifficulty = logs.stream().mapToInt(TourLogEntity::getDifficulty).average().orElse(0);
        double differenceScore = 6 - averageDifficulty; // Bereich: [1, 5]

        // Durchschnittliche Zeit in Sekunden
        double averageTimeInSeconds = logs.stream().mapToDouble(tourLogEntity -> tourLogEntity.getTotalTime().getSeconds()).average().orElse(0);
        double averegeTimeInHours = averageTimeInSeconds / 3600.00;
        // Kürzere Tour = höherer Score, z.B. max 10h = Score bis ~10,
        double timeScore = Math.max(0, 10.0 - averegeTimeInHours); // Bereich: [0, 10]

        // Durchschnittliche Distanz in Metern
        double averageDistance = logs.stream().mapToDouble(TourLogEntity::getTotalDistance).average().orElse(0);
        // Kürzere Strecke = höherer Score, z.B. max 10 km → Score bis ~10
        double distanceScore =  Math.max(0, 10.0 - averageDistance);


        // Durchschnittliche Bewertung: je niedriger Difficulty, desto höher die Bewertung
        return differenceScore + timeScore + distanceScore;
    }

    private void validateTourLog(TourLogEntity tourLogEntity) throws SQLException {
        if(tourLogEntity.getDifficulty() < 1 || tourLogEntity.getDifficulty() > 5) {
            throw new SQLException("Difficulty must be between 1 and 5");
        }

        if (tourLogEntity.getRating() < 1 || tourLogEntity.getRating() > 5) {
            throw new SQLException("Rating must be between 1 and 5");
        }
    }

}
