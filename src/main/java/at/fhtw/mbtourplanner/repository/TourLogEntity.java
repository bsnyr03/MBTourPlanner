package at.fhtw.mbtourplanner.repository;


import at.fhtw.mbtourplanner.converter.DurationToIntervalConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import at.fhtw.mbtourplanner.converter.DurationToIntervalSerializer;
import at.fhtw.mbtourplanner.converter.DurationToIntervalDeserializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name="tour_logs")
public class TourLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tour_id", nullable = false)
    private TourEntity tour;

    @Column(name = "log_datetime", nullable = false)
    private LocalDateTime logDateTime;

    @Column(length = 2000)
    private String comment;

    @Column(nullable = false)
    private int difficulty;

    @Column(name = "total_distance", nullable = false)
    private double totalDistance;

    @JsonSerialize(using = DurationToIntervalSerializer.class)
    @JsonDeserialize(using = DurationToIntervalDeserializer.class)
    @Column(name = "total_time", nullable = false)
    @Convert(converter = DurationToIntervalConverter.class)
    private Duration totalTime;

    @Column(nullable = false)
    private int rating;

}
