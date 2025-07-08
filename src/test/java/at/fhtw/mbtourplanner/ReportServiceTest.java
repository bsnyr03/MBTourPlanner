package at.fhtw.mbtourplanner;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.model.TourLog;
import at.fhtw.mbtourplanner.repository.TourEntity;
import at.fhtw.mbtourplanner.repository.TourRepository;
import at.fhtw.mbtourplanner.service.OpenRouteService;
import at.fhtw.mbtourplanner.service.ReportService;
import at.fhtw.mbtourplanner.service.TourLogService;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReportServiceTest {

    @Mock
    private TourService tourService;

    @Mock
    private TourLogService tourLogService;

    @Mock
    private OpenRouteService openRouteService;

    @InjectMocks
    private ReportService reportService;

    private Tour sampleTour;
    private List<TourLog> sampleLogs;

    @BeforeEach
    void setUp() throws SQLException {
        sampleTour = Tour.builder()
                .id(1L)
                .name("Test Tour")
                .description("Desc")
                .fromLocation("A")
                .toLocation("B")
                .transportType("walk")
                .distance(5.0)
                .estimatedTime(Duration.ofMinutes(30))
                .routeImageUrl("")  // no image
                .popularity(0)
                .childFriendliness(0.0)
                .build();

        TourLog log = TourLog.builder()
                .id(1L)
                .logDateTime(LocalDateTime.of(2025,6,30,10,0))
                .comment("OK")
                .difficulty(2)
                .totalDistance(5.0)
                .totalTime(Duration.ofMinutes(30))
                .rating(4)
                .build();

        sampleLogs = List.of(log);

        given(tourService.getTourById(1L)).willReturn(sampleTour);
        given(tourLogService.getLogsForTour(1L)).willReturn(sampleLogs);

        given(openRouteService.getRouteInfo(anyString(),anyList()))
                .willReturn(Map.of("route", List.of(List.of(0.0, 0.0), List.of(1.0, 1.0))));
    }


    @Test
    void generateTourReportPDF_whenTourNotFound_shouldThrowSQLException() throws SQLException {
        given(tourService.getTourById(2L)).willReturn(null);
        SQLException ex = assertThrows(SQLException.class, () -> reportService.generateTourReportPDF(2L));
        assertThat(ex.getMessage()).contains("Tour not found with ID: 2");
    }

    @Test
    void generateSummaryReportPDF_shouldReturnPdfBytes() throws Exception {
        given(tourService.getAllTours()).willReturn(List.of(sampleTour));
        given(tourLogService.getLogsForTour(1L)).willReturn(sampleLogs);

        byte[] pdf = reportService.generateSummaryReportPDF();
        assertThat(pdf).isNotNull();
        assertThat(pdf.length).isGreaterThan(0);
        String header = new String(pdf, 0, 4);
        assertThat(header).isEqualTo("%PDF");
    }
}
