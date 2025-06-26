package at.fhtw.mbtourplanner.model;


import at.fhtw.mbtourplanner.repository.TourEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
public class TourLog {
    private Long id;

    @JsonIgnore
    private TourEntity tour;

    @NotNull(message = "Log date and time is missing")
    private LocalDateTime logDateTime;

    @NotBlank(message = "Comment is missing")
    private String comment;

    @NotNull(message = "Difficulty is missing")
    private int difficulty;

    @NotNull(message = "Total distance is missing")
    private double totalDistance;

    @NotNull(message = "Total time is missing")
    private Duration totalTime;

    @NotBlank(message = "Rating is missing")
    private String rating;

}
