package at.fhtw.mbtourplanner.model;
import lombok.*;
import org.postgresql.util.PGInterval;

@Data
@Builder

public class Tour {
    private String name;
    private String description;
    private String fromLocation;
    private String toLocation;
    private String transportType;
    private double distance;
    private PGInterval estimatedTime;
    private String routeImageUrl;
}