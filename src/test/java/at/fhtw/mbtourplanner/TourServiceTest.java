package at.fhtw.mbtourplanner;


import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.repository.TourEntity;
import at.fhtw.mbtourplanner.repository.TourLogRepository;
import at.fhtw.mbtourplanner.repository.TourRepository;
import at.fhtw.mbtourplanner.service.TourMapper;
import at.fhtw.mbtourplanner.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TourServiceTest {

    @Mock
    private TourRepository tourRepository;

    @Mock private TourLogRepository tourLogRepository;

    @Mock private TourMapper tourMapper;

    @InjectMocks
    private TourService tourService;

    private TourEntity sampleEntity;
    private Tour sampleDto;

    @BeforeEach
    void setUp() throws SQLException {
        sampleEntity = new TourEntity();
        sampleEntity.setId(1L);
        sampleEntity.setName("Sample Name");
        sampleEntity.setDescription("Sample Description");
        sampleEntity.setFromLocation("Sample From");
        sampleEntity.setToLocation("Sample To");
        sampleEntity.setTransportType("Car");
        sampleEntity.setDistance(1000);
        sampleEntity.setEstimatedTime(Duration.ofHours(1));
        sampleEntity.setRouteImageUrl("Sample Route Image");
        sampleEntity.setPopularity(3);
        sampleEntity.setChildFriendliness(4);


        sampleDto = Tour.builder().name("Sample").build();

        when(tourMapper.toDto(sampleEntity)).thenReturn(sampleDto);
        when(tourMapper.toDto(anyList())).thenReturn(List.of(sampleDto));
        when(tourMapper.toEntity(sampleDto)).thenReturn(sampleEntity);
    }

    @Test
    void getAllTours_shouldMapAllEntities() throws SQLException {
        when(tourRepository.findAll()).thenReturn(List.of(sampleEntity));

        var result = tourService.getAllTours();

        assertThat(result)
                .hasSize(1)
                .first().isSameAs(sampleDto);

        verify(tourRepository).findAll();
    }

    @Test
    void searchTours_shouldDelegateToRepository() throws SQLException {
        String q = "Sample";

        when(tourRepository.searchTours(q)).thenReturn(List.of(sampleEntity));

        var result = tourService.searchTours(q);

        assertThat(result)
                .hasSize(1)
                .first().isSameAs(sampleDto);

        verify(tourRepository).searchTours(q);
    }

    @Test
    void addTour_shouldSaveMappedEntity() throws SQLException {
        tourService.addTour(sampleDto);

        verify(tourMapper).toEntity(sampleDto);
        verify(tourRepository).save(sampleEntity);
    }

    @Test
    void getTourById_existingId_shouldReturnMappedDto() throws SQLException {
        Long id = 1L;

        when(tourRepository.findById(id)).thenReturn(Optional.of(sampleEntity));

        var result = tourService.getTourById(id);

        assertThat(result).isSameAs(sampleDto);
        verify(tourRepository).findById(id);
        verify(tourMapper).toDto(sampleEntity);
    }

    @Test
    void getTourById_unknownId_shouldThrowException() throws SQLException {
        Long id = 42L;

        when(tourRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tourService.getTourById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Tour not found");

        verify(tourRepository).findById(id);
        verifyNoMoreInteractions(tourMapper);
    }

    @Test
    void updateTour_existingId_shouldSaveandReturnUpdatedDto() throws SQLException {
       TourEntity existingEntity = new TourEntity();

       existingEntity.setId(2L);
       existingEntity.setName("Old");

       Tour updateDto = Tour.builder()
               .name("New")
               .description("New Description")
               .fromLocation("New From")
               .toLocation("New To")
               .transportType("Bike")
               .distance(2000)
               .estimatedTime(Duration.ofHours(2))
               .routeImageUrl("New Route Image")
               .popularity(5)
               .childFriendliness(4)
               .build();

       when(tourRepository.findById(2L)).thenReturn(Optional.of(existingEntity));
       when(tourRepository.save(existingEntity)).thenReturn(existingEntity);
       when(tourMapper.toDto(existingEntity)).thenReturn(updateDto);

       var result = tourService.updateTour(2L, updateDto);

       assertThat(result).isSameAs(updateDto);
       assertThat(existingEntity.getName()).isEqualTo("New");
       assertThat(existingEntity.getDescription()).isEqualTo("New Description");

       verify(tourRepository).findById(2L);
       verify(tourRepository).save(existingEntity);
       verify(tourMapper).toDto(existingEntity);
    }

    @Test
    void updateTour_unknownId_shouldThrowException() throws SQLException {
        when(tourRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tourService.updateTour(99L, sampleDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Tour not found");

        verify(tourRepository).findById(99L);
    }

    @Test
    void deleteTour_existingId_shouldDeleteEntity() throws SQLException {
        when(tourRepository.findById(3L)).thenReturn(Optional.of(sampleEntity));

        tourService.deleteTour(3L);

        verify(tourRepository).findById(3L);
        verify(tourRepository).delete(sampleEntity);
    }

    @Test
    void deleteTour_unknownId_shouldThrow() {
        when(tourRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tourService.deleteTour(77L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Tour not found");

        verify(tourRepository).findById(77L);
    }
}
