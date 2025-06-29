package at.fhtw.mbtourplanner.controller;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.repository.TourEntity;
import at.fhtw.mbtourplanner.service.TourMapper;
import at.fhtw.mbtourplanner.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TourControllerTest {

    @Mock
    private TourService tourService;

    @Mock
    private TourMapper tourMapper;

    @InjectMocks
    private TourController controller;

    private Tour sampleDto;
    private TourEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleDto = Tour.builder()
                .name("Sample")
                .description("Desc")
                .fromLocation("A")
                .toLocation("B")
                .transportType("walk")
                .distance(1.5)
                .estimatedTime(Duration.ofMinutes(30))
                .routeImageUrl("url")
                .popularity(2)
                .childFriendliness(3.0)
                .build();

        sampleEntity = new TourEntity();
        sampleEntity.setName(sampleDto.getName());
        sampleEntity.setDescription(sampleDto.getDescription());
        sampleEntity.setFromLocation(sampleDto.getFromLocation());
        sampleEntity.setToLocation(sampleDto.getToLocation());
        sampleEntity.setTransportType(sampleDto.getTransportType());
        sampleEntity.setDistance(sampleDto.getDistance());
        sampleEntity.setEstimatedTime(Duration.parse(sampleDto.getEstimatedTime().toString()));
        sampleEntity.setRouteImageUrl(sampleDto.getRouteImageUrl());
        sampleEntity.setPopularity(sampleDto.getPopularity());
        sampleEntity.setChildFriendliness(sampleDto.getChildFriendliness());
    }

    @Test
    void getAll_ReturnsList() throws SQLException {
        given(tourService.getAllTours()).willReturn(List.of(sampleDto));

        List<Tour> result = controller.getAll();

        assertThat(result).containsExactly(sampleDto);
        verify(tourService).getAllTours();
    }

    @Test
    void addTour_DelegatesToService() throws SQLException {
        doNothing().when(tourService).addTour(sampleDto);

        controller.addTour(sampleDto);

        verify(tourService).addTour(sampleDto);
    }

    @Test
    void getById_ReturnsTour() throws SQLException {
        given(tourService.getTourById(1L)).willReturn(sampleDto);

        Tour result = controller.getTourById(1L);

        assertThat(result).isSameAs(sampleDto);
        verify(tourService).getTourById(1L);
    }

    @Test
    void updateTour_ReturnsUpdated() throws SQLException {
        given(tourService.updateTour(1L, sampleDto)).willReturn(sampleDto);

        Tour result = controller.updateTour(1L, sampleDto);

        assertThat(result).isSameAs(sampleDto);
        verify(tourService).updateTour(1L, sampleDto);
    }

    @Test
    void deleteTour_CallsService() throws SQLException {
        doNothing().when(tourService).deleteTour(2L);

        controller.deleteTour(2L);

        verify(tourService).deleteTour(2L);
    }

    @Test
    void searchTours_ReturnsMatches() {
        given(tourService.searchTours("q")).willReturn(List.of(sampleDto));

        List<Tour> result = controller.searchTours("q");

        assertThat(result).containsExactly(sampleDto);
        verify(tourService).searchTours("q");
    }

    @Test
    void exportAllToursJSON_ReturnsResponseEntity() throws SQLException {
        given(tourService.getAllTours()).willReturn(List.of(sampleDto));

        ResponseEntity<List<Tour>> resp = controller.exportALlToursJSON();

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=tours.json");
        assertThat(resp.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(resp.getBody()).containsExactly(sampleDto);
    }

    @Test
    void importAllToursJSON_ReturnsImportedCount() throws SQLException {
        Map<String, Integer> resp = controller.importAllToursJSON(List.of(sampleDto)).getBody();

        assertThat(resp.get("imported")).isEqualTo(1);
        assertThat(resp.get("status")).isEqualTo(HttpStatus.OK.value());
        verify(tourService).addTour(sampleDto);
    }

    @Test
    void exportAllToursCSV_ReturnsCsvBytes() throws SQLException {
        given(tourService.getAllTours()).willReturn(List.of(sampleDto));

        ResponseEntity<byte[]> resp = controller.exportAllToursCSV();
        String csv = new String(resp.getBody(), StandardCharsets.UTF_8);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION))
                .isEqualTo("attachment; filename=tours.csv");
        assertThat(resp.getHeaders().getContentType().toString()).isEqualTo("text/csv");
        assertThat(csv).contains("Sample,Desc,A,B,walk,1.5,PT0H30M,url,2,3.0");
    }

    @Test
    void importAllToursCSV_ReturnsImportedCount() throws Exception {
        String csv = "id,name, description, fromLocation, toLocation, transportType, distance, estimatedTime, routeImageURL, popularity, childFriendliness\n"
                + " ,Sample,Desc,A,B,walk,1.5,PT0H30M,url,2,3.0\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "tours.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8)
        );
        // stub mapper and service
        given(tourMapper.toDto(any(TourEntity.class))).willReturn(sampleDto);
        doNothing().when(tourService).addTour(sampleDto);

        ResponseEntity<Map<String, Integer>> resp = controller.importAllToursCSV(file);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().get("imported")).isEqualTo(1);
        assertThat(resp.getBody().get("status")).isEqualTo(HttpStatus.OK.value());
    }
}