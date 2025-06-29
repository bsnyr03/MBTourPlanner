package at.fhtw.mbtourplanner.controller;

import at.fhtw.mbtourplanner.model.Tour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.openMocks;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        openMocks(this);
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationErrors_returnsBadRequestWithFieldErrors() throws NoSuchMethodException {
        FieldError fe1 = new FieldError("tour", "name", "Name is required");
        FieldError fe2 = new FieldError("tour", "distance", "Distance must be positive");
        given(bindingResult.getFieldErrors()).willReturn(List.of(fe1, fe2));

        Method targetMethod = TourController.class.getMethod("addTour", Tour.class);
        MethodParameter methodParam = new MethodParameter(targetMethod, 0);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParam, bindingResult);

        ResponseEntity<Map<String, String>> resp = handler.handleValidationErrors(ex);

        assertThat(resp.getStatusCodeValue()).isEqualTo(400);
        Map<String, String> body = resp.getBody();
        assertThat(body).containsEntry("name", "Name is required");
        assertThat(body).containsEntry("distance", "Distance must be positive");
    }

    @Test
    void handleNotFound_returnsNotFoundWithErrorMessage() {
        RuntimeException rex = new RuntimeException("Resource not found");

        ResponseEntity<Map<String, String>> resp = handler.handleNotFound(rex);

        assertThat(resp.getStatusCodeValue()).isEqualTo(404);
        assertThat(resp.getBody()).containsEntry("error", "Resource not found");
    }
}