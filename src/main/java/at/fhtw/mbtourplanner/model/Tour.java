package at.fhtw.mbtourplanner.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Duration;

@Data
@Builder
public class Tour {
    private Long id;

    @NotBlank(message = "Name is missing")
    private String name;

    @NotBlank(message = "Description is missing")
    private String description;

    @NotBlank(message = "fromlocation is missing")
    private String fromLocation;

    @NotBlank(message = "tolocation is missing")
    private String toLocation;

    @NotBlank(message = "Transport type is missing")
    private String transportType;

    private double distance;

    private Duration estimatedTime;

    private String routeImageUrl;

    private double fromLat;
    private double fromLon;
    private double toLat;
    private double toLon;

    @NotNull(message = "popularity is missing")
    private int popularity;

    @NotNull(message = "childFriendliness is missing")
    private double childFriendliness;

}