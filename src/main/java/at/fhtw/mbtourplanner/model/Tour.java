package at.fhtw.mbtourplanner.model;
import lombok.*;

@Data
@Builder

public class Tour {
    private String name;
    private String description;
    private String fromLocation;
    private String toLocation;
    private String transportType;
    private double distance;
    private String estimatedTime;
    private String routeImageUrl;
}