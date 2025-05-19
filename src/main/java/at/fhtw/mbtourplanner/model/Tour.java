package at.fhtw.mbtourplanner.model;
import lombok.*;

import java.time.Duration;

@Data
@Builder

public class Tour {
    private String name;
    private String description;
    private String fromLocation;
    private String toLocation;
    private String transportType;
    private double distance;
    private Duration estimatedTime;
    private String routeImageUrl;
}