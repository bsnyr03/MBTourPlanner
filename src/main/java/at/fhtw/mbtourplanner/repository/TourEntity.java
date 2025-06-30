package at.fhtw.mbtourplanner.repository;
import at.fhtw.mbtourplanner.converter.DurationToIntervalConverter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import at.fhtw.mbtourplanner.converter.DurationToIntervalSerializer;
import at.fhtw.mbtourplanner.converter.DurationToIntervalDeserializer;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "tours")
public class TourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "from_location",nullable = false)
    private String fromLocation;

    @Column(name = "to_location", nullable = false)
    private String toLocation;

    @Column(name = "transport_type", nullable = false)
    private String transportType;

    private double distance;

    @JsonSerialize(using = DurationToIntervalSerializer.class)
    @JsonDeserialize(using = DurationToIntervalDeserializer.class)
    @Column(name = "estimated_time")
    @Convert(converter = DurationToIntervalConverter.class)
    private Duration estimatedTime;

    @Column(name = "route_image_url")
    private String routeImageUrl;

    @Column(name = "from_latitude")
    private double fromLat;

    @Column(name = "from_longitude")
    private double fromLon;

    @Column(name = "to_latitude")
    private double toLat;

    @Column(name = "to_longitude")
    private double toLon;

    @Column(name = "popularity")
    private int popularity;

    @Column(name = "child_friendliness")
    private double childFriendliness;
}
