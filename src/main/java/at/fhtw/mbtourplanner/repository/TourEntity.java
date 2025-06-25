package at.fhtw.mbtourplanner.repository;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "tours")
public class TourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Column(name = "estimated_time")
    private String estimatedTime;

    @Column(name = "route_image_url")
    private String routeImageUrl;

    @Column(name = "popularity")
    private int popularity;

    @Column(name = "child_friendliness")
    private double childFriendliness;
}
