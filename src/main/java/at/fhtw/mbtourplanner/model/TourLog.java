package at.fhtw.mbtourplanner.model;


import at.fhtw.mbtourplanner.repository.TourEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
public class TourLog {
    private Long id;

    @JsonIgnore
    private TourEntity tour;

    private LocalDateTime logDateTime;
    private String comment;
    private int difficulty;
    private double totalDistance;
    private String totalTime;
    private String rating;

}
