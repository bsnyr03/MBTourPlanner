package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.TourLog;
import at.fhtw.mbtourplanner.repository.TourLogEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class TourLogMapper extends AbstractMapper<TourLogEntity, TourLog> {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public TourLog toDto(TourLogEntity entity) {
        return TourLog.builder()
                .id(entity.getId())
                .tour(entity.getTour())
                .logDateTime(entity.getLogDateTime())
                .comment(entity.getComment())
                .difficulty(entity.getDifficulty())
                .totalDistance(entity.getTotalDistance())
                .totalTime(entity.getTotalTime())
                .rating(entity.getRating())
                .build();
    }

    @Override
    public TourLogEntity toEntity(TourLog dto) {
        return TourLogEntity.builder()
                .id(dto.getId())
                .tour(dto.getTour())
                .logDateTime(dto.getLogDateTime())
                .comment(dto.getComment())
                .difficulty(dto.getDifficulty())
                .totalDistance(dto.getTotalDistance())
                .totalTime(dto.getTotalTime())
                .rating(dto.getRating())
                .build();
    }


}
