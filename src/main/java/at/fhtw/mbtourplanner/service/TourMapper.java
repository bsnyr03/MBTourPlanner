package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.repository.TourEntity;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class TourMapper extends AbstractMapper<TourEntity, Tour> {

    @Override
    public Tour toDto(TourEntity entity) throws SQLException {
        return Tour.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .fromLocation(entity.getFromLocation())
                .toLocation(entity.getToLocation())
                .transportType(entity.getTransportType())
                .distance(entity.getDistance())
                .estimatedTime(entity.getEstimatedTime())
                .routeImageUrl(entity.getRouteImageUrl())
                .fromLat(entity.getFromLat())
                .fromLon(entity.getFromLon())
                .toLat(entity.getToLat())
                .toLon(entity.getToLon())
                .popularity(entity.getPopularity())
                .childFriendliness(entity.getChildFriendliness())
                .build();
    }

    @Override
    public TourEntity toEntity(Tour dto) throws SQLException {
        return TourEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .fromLocation(dto.getFromLocation())
                .toLocation(dto.getToLocation())
                .transportType(dto.getTransportType())
                .distance(dto.getDistance())
                .estimatedTime(dto.getEstimatedTime())
                .routeImageUrl(dto.getRouteImageUrl())
                .fromLat(dto.getFromLat())
                .fromLon(dto.getFromLon())
                .toLat(dto.getToLat())
                .toLon(dto.getToLon())
                .popularity(dto.getPopularity())
                .childFriendliness(dto.getChildFriendliness())
                .build();
    }
}

