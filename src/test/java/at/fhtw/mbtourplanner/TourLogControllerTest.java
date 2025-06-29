package at.fhtw.mbtourplanner.controller;

import at.fhtw.mbtourplanner.model.TourLog;
import at.fhtw.mbtourplanner.service.TourLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TourLogControllerTest {

    @Mock
    private TourLogService tourLogService;

    @InjectMocks
    private TourLogController controller;

    private TourLog sampleLog;

    @BeforeEach
    void setUp() {
        sampleLog = TourLog.builder()
                .id(1L)
                .logDateTime(LocalDateTime.of(2025, 1, 1, 10, 0))
                .comment("Great")
                .difficulty(3)
                .totalDistance(12.5)
                .totalTime(Duration.ofHours(2))
                .rating(4)
                .build();
    }

    @Test
    void getAll_ReturnsLogs() throws SQLException {
        given(tourLogService.getLogsForTour(10L)).willReturn(List.of(sampleLog));

        List<TourLog> result = controller.getAll(10L);

        assertThat(result).containsExactly(sampleLog);
        verify(tourLogService).getLogsForTour(10L);
    }

    @Test
    void getOneLog_ReturnsLog() throws SQLException {
        given(tourLogService.getLog(10L, 1L)).willReturn(sampleLog);

        TourLog result = controller.getOneLog(10L, 1L);

        assertThat(result).isSameAs(sampleLog);
        verify(tourLogService).getLog(10L, 1L);
    }

    @Test
    void create_ReturnsCreatedLog() throws SQLException {
        given(tourLogService.addLog(10L, sampleLog)).willReturn(sampleLog);

        TourLog result = controller.create(10L, sampleLog);

        assertThat(result).isSameAs(sampleLog);
        verify(tourLogService).addLog(10L, sampleLog);
    }

    @Test
    void update_ReturnsUpdatedLog() throws SQLException {
        given(tourLogService.updateLog(10L, 1L, sampleLog)).willReturn(sampleLog);

        TourLog result = controller.update(10L, 1L, sampleLog);

        assertThat(result).isSameAs(sampleLog);
        verify(tourLogService).updateLog(10L, 1L, sampleLog);
    }

    @Test
    void delete_CallsService() throws SQLException {
        doNothing().when(tourLogService).deleteLog(10L, 1L);

        controller.delete(10L, 1L);

        verify(tourLogService).deleteLog(10L, 1L);
    }

    @Test
    void search_ReturnsFiltered() throws SQLException {
        given(tourLogService.searchLogs(10L, "q")).willReturn(List.of(sampleLog));

        List<TourLog> result = controller.search(10L, "q");

        assertThat(result).containsExactly(sampleLog);
        verify(tourLogService).searchLogs(10L, "q");
    }
}