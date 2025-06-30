package at.fhtw.mbtourplanner.service;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OpenRouteService {
    private final WebClient webClient;

    public OpenRouteService(WebClient.Builder builder,
                            @Value("${tours.ors-base-url}") String baseUrl,
                            @Value("${tours.ors-api-key}") String apiKey) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", apiKey)
                .build();
    }

    public Map<String, Double> getRouteInfo(String profile, List<List<Double>> coords) {
        log.info("ORS request for profile={} coords={}", profile, coords);
        @SuppressWarnings("unchecked")
        Map<String,Object> resp = webClient.post()
                .uri("/v2/directions/{profile}", profile)
                .bodyValue(Map.of("coordinates", coords))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null) {
            log.error("ORS returned null for profile={} coords={}", profile, coords);
            throw new RuntimeException("Empty ORS response");
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> features = (List<Map<String, Object>>) resp.get("features");
        if (features.isEmpty()) {
            log.error("ORS returned no features for profile={} coords={}", profile, coords);
            throw new RuntimeException("No route features");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) features.get(0).get("properties");
        @SuppressWarnings("unchecked")
        Map<String, Object> summary = (Map<String, Object>) properties.get("summary");
        double distance = ((Number) summary.get("distance")).doubleValue();
        double duration = ((Number) summary.get("duration")).doubleValue();
        log.info("ORS result: distance={} meters, duration={} seconds", distance, duration);
        return Map.of("distance", distance, "duration", duration);
    }

    public String getMapTileUrl(double lon, double lat, int zoom) {
        int n = 1 << zoom;
        double xTile = (lon + 180.0) / 360.0 * n;
        double latRad = Math.toRadians(lat);
        double yTile = (1.0 - Math.log(Math.tan(latRad) + 1.0 / Math.cos(latRad)) / Math.PI) / 2.0 * n;
        int x = (int) Math.floor(xTile);
        int y = (int) Math.floor(yTile);
        String url = String.format("https://tile.openstreetmap.org/%d/%d/%d.png", zoom, x, y);
        log.info("Generated OSM tile URL: {}", url);
        return url;
    }
}