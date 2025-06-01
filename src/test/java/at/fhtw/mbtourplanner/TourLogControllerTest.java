package at.fhtw.mbtourplanner;

import at.fhtw.mbtourplanner.controller.TourLogController;
import at.fhtw.mbtourplanner.model.TourLog;
import at.fhtw.mbtourplanner.service.TourLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TourLogController.class)
class TourLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TourLogService tourLogService;

    @Test
    void getAllTourLogs_returnsOk() throws Exception {
        when(tourLogService.getLogsForTour(1L)).thenReturn(List.of(
                TourLog.builder().id(1L).comment("Test Log").build()
        ));

        mockMvc.perform(get("/api/tours/1/tour_logs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTourLogById_returnsOk() throws Exception {
        when(tourLogService.getLog(1L, 2L)).thenReturn(
                TourLog.builder().id(2L).comment("Test Log").build()
        );

        mockMvc.perform(get("/api/tours/1/tour_logs/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void getTourLogById_notFound() throws Exception {
        when(tourLogService.getLog(1L, 99L)).thenReturn(null);

        mockMvc.perform(get("/api/tours/1/tour_logs/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addTourLog_returnsCreated() throws Exception {
        TourLog createdLog = TourLog.builder().id(3L).comment("New Log").build();
        when(tourLogService.addLog(eq(1L), any(TourLog.class))).thenReturn(createdLog);
        String json = "{\"comment\":\"New Log\"}";

        mockMvc.perform(post("/api/tours/1/tour_logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.comment").value("New Log"));
    }

    @Test
    void updateTourLog_returnsOk() throws Exception {
        TourLog updatedLog = TourLog.builder().id(2L).comment("Updated Log").build();
        when(tourLogService.updateLog(eq(1L), eq(2L), any(TourLog.class))).thenReturn(updatedLog);

        String json = "{\"comment\":\"Updated Log\"}";

        mockMvc.perform(put("/api/tours/1/tour_logs/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.comment").value("Updated Log"));
    }

    @Test
    void deleteTourLog_returnsNoContent() throws Exception {
        doNothing().when(tourLogService).deleteLog(1L, 2L);

        mockMvc.perform(delete("/api/tours/1/tour_logs/2"))
                .andExpect(status().isNoContent());
    }
}