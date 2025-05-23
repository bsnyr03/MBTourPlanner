package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.repository.TourEntity;
import org.apache.catalina.mapper.Mapper;
import org.postgresql.util.PGInterval;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class TourMapper extends AbstractMapper<TourEntity, Tour> {

    @Override
    public Tour toDto(TourEntity entity) throws SQLException {
        return Tour.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .fromLocation(entity.getFromLocation())
                .toLocation(entity.getToLocation())
                .transportType(entity.getTransportType())
                .distance(entity.getDistance())
                .estimatedTime(entity.getEstimatedTime() != null ? new PGInterval(entity.getEstimatedTime().toString()) : null)
                .routeImageUrl(entity.getRouteImageUrl())
                .build();
    }

    @Override
    public TourEntity toEntity(Tour dto) throws SQLException {
        return TourEntity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .fromLocation(dto.getFromLocation())
                .toLocation(dto.getToLocation())
                .transportType(dto.getTransportType())
                .distance(dto.getDistance())
                .estimatedTime(dto.getEstimatedTime() != null ? new PGInterval(dto.getEstimatedTime().toString()) : null)
                .routeImageUrl(dto.getRouteImageUrl())
                .build();
    }
}
