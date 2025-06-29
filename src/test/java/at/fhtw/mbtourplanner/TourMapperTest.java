package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.repository.TourEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TourMapperTest {
    private TourMapper mapper;
    private TourEntity sampleEntity;
    private Tour sampleDto;

    @BeforeEach
    void setUp() {
        mapper = new TourMapper();

        sampleEntity = TourEntity.builder()
                .id(1L)
                .name("City Tour")
                .description("A city bike tour.")
                .fromLocation("Start")
                .toLocation("End")
                .transportType("bike")
                .distance(12.5)
                .estimatedTime(Duration.ofHours(1).plusMinutes(30))
                .routeImageUrl("http://example.com/map.png")
                .popularity(7)
                .childFriendliness(4.5)
                .build();

        sampleDto = Tour.builder()
                .id(2L)
                .name("Mountain Hike")
                .description("Hike up the mountain.")
                .fromLocation("Base")
                .toLocation("Summit")
                .transportType("hike")
                .distance(8.0)
                .estimatedTime(Duration.ofHours(2))
                .routeImageUrl("http://example.com/hike.png")
                .popularity(5)
                .childFriendliness(3.2)
                .build();
    }

    @Test
    void toDto_shouldMapAllFields() throws SQLException {
        Tour dto = mapper.toDto(sampleEntity);

        assertThat(dto.getId()).isEqualTo(sampleEntity.getId());
        assertThat(dto.getName()).isEqualTo(sampleEntity.getName());
        assertThat(dto.getDescription()).isEqualTo(sampleEntity.getDescription());
        assertThat(dto.getFromLocation()).isEqualTo(sampleEntity.getFromLocation());
        assertThat(dto.getToLocation()).isEqualTo(sampleEntity.getToLocation());
        assertThat(dto.getTransportType()).isEqualTo(sampleEntity.getTransportType());
        assertThat(dto.getDistance()).isEqualTo(sampleEntity.getDistance());
        assertThat(dto.getEstimatedTime()).isEqualTo(sampleEntity.getEstimatedTime());
        assertThat(dto.getRouteImageUrl()).isEqualTo(sampleEntity.getRouteImageUrl());
        assertThat(dto.getPopularity()).isEqualTo(sampleEntity.getPopularity());
        assertThat(dto.getChildFriendliness()).isEqualTo(sampleEntity.getChildFriendliness());
    }

    @Test
    void toEntity_shouldMapAllFields() throws SQLException {
        TourEntity entity = mapper.toEntity(sampleDto);

        assertThat(entity.getId()).isEqualTo(sampleDto.getId());
        assertThat(entity.getName()).isEqualTo(sampleDto.getName());
        assertThat(entity.getDescription()).isEqualTo(sampleDto.getDescription());
        assertThat(entity.getFromLocation()).isEqualTo(sampleDto.getFromLocation());
        assertThat(entity.getToLocation()).isEqualTo(sampleDto.getToLocation());
        assertThat(entity.getTransportType()).isEqualTo(sampleDto.getTransportType());
        assertThat(entity.getDistance()).isEqualTo(sampleDto.getDistance());
        assertThat(entity.getEstimatedTime()).isEqualTo(sampleDto.getEstimatedTime());
        assertThat(entity.getRouteImageUrl()).isEqualTo(sampleDto.getRouteImageUrl());
        assertThat(entity.getPopularity()).isEqualTo(sampleDto.getPopularity());
        assertThat(entity.getChildFriendliness()).isEqualTo(sampleDto.getChildFriendliness());
    }

    @Test
    void toDtoList_shouldMapCollection() throws SQLException {
        List<Tour> dtos = mapper.toDto(List.of(sampleEntity, sampleEntity));
        assertThat(dtos).hasSize(2)
                .allMatch(t -> t.getName().equals(sampleEntity.getName()));
    }
}