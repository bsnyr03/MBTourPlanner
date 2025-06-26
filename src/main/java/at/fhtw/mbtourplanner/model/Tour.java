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

    @NotNull(message = "Distance is missing")
    private double distance;

    @NotNull(message = "Estimated time is missing")
    private Duration estimatedTime;

    @NotBlank(message = "Route image URL is missing")
    private String routeImageUrl;

    @NotNull(message = "popularity is missing")
    private int popularity;

    @NotNull(message = "childFriendliness is missing")
    private double childFriendliness;

}