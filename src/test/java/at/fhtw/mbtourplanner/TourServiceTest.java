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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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







}
