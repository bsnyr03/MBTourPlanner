package at.fhtw.mbtourplanner;

import at.fhtw.mbtourplanner.controller.TourController;
import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.service.TourService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TourController.class)
class TourControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TourService tourService;

    @Test
    void getAllTours_returnsOk() throws Exception {
        when(tourService.getAllTours()).thenReturn(List.of(
                Tour.builder().id(1L).name("Test Tour").build()
        ));

        mockMvc.perform(get("/api/tours"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTourById_returnsOk() throws Exception {
        when(tourService.getTourById(1L)).thenReturn(
                Tour.builder().id(1L).name("Test Tour").build()
        );

        mockMvc.perform(get("/api/tours/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getTourById_notFound() throws Exception {
        when(tourService.getTourById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/tours/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addTour_returnsCreated() throws Exception {
        doNothing().when(tourService).addTour(org.mockito.ArgumentMatchers.any(Tour.class));
        String json = "{\"name\":\"New Tour\"}";

        mockMvc.perform(post("/api/tours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());}

    @Test
    void updateTour_returnsOk() throws Exception {
        Tour updatedTour = Tour.builder().id(1L).name("Updated Tour").build();
        when(tourService.updateTour(1L, org.mockito.ArgumentMatchers.any(Tour.class))).thenReturn(updatedTour);

        String json = "{\"name\":\"Updated Tour\"}";

        mockMvc.perform(put("/api/tours/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Tour"));
    }

    @Test
    void deleteTour_returnsNoContent() throws Exception {
        doNothing().when(tourService).deleteTour(1L);

        mockMvc.perform(delete("/api/tours/1"))
                .andExpect(status().isNoContent());
    }
}