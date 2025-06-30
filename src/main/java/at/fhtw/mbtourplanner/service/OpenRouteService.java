package at.fhtw.mbtourplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    public Map<String, Object> getRouteInfo(String profile, List<List<Double>> coords) {
        log.info("ORS request for profile={} coords={}", profile, coords);
        Map<String, Object> body = Map.of("coordinates", coords);

        JsonNode root = webClient.post()
                .uri("/" + profile + "/geojson")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        JsonNode features = root.path("features").get(0);

        JsonNode summary = features.path("properties").path("summary");
        double distance = summary.path("distance").asDouble();
        double duration = summary.path("duration").asDouble();

        List<List<Double>> line = new ArrayList<>();

        for(JsonNode coord : features.path("geometry").path("coordinates")) {
            line.add(List.of(coord.get(1).asDouble(), coord.get(0).asDouble()));
        }

        log.info("ORS response: distance={} duration={} line={}", distance, duration, line);

        return Map.of("distance", distance,
                       "duration", duration,
                       "route", line);

    }

    public String getStaticRouteMapUrl(List<List<Double>> route, int width, int height, int zoom) {
        double avgLat = route.stream().mapToDouble(pt -> pt.get(0)).average().orElse(0);
        double avgLon = route.stream().mapToDouble(pt -> pt.get(1)).average().orElse(0);

        String rawPolyline = PolyLineEncoder.encode(route);
        String encodedPolyline = URLEncoder.encode(rawPolyline, StandardCharsets.UTF_8);

        StringBuilder sb = new StringBuilder("https://staticmap.openstreetmap.de/staticmap.php");
        sb.append("?size=").append(width).append("x").append(height);
        sb.append("&center=").append(avgLat).append(",").append(avgLon);
        sb.append("&zoom=").append(zoom);

        List<Double> start = route.get(0);
        List<Double> end   = route.get(route.size() - 1);
        sb.append("&markers=")
                .append(start.get(0)).append(",").append(start.get(1)).append(",blue1|")
                .append(end.get(0)).append(",").append(end.get(1)).append(",red1");

        sb.append("&path=enc:").append(encodedPolyline);

        return sb.toString();
    }

    public String buildStaticMapUrl(
            List<List<Double>> route,
            int width, int height,
            int zoom
    ) {
        double avgLat = route.stream()
                .mapToDouble(pt -> pt.get(0))
                .average().orElse(0);
        double avgLon = route.stream()
                .mapToDouble(pt -> pt.get(1))
                .average().orElse(0);

        String encoded = PolyLineEncoder.encode(route);

        StringBuilder sb = new StringBuilder("https://staticmap.openstreetmap.de/staticmap.php");
        sb.append("?size=").append(width).append("x").append(height);
        sb.append("&center=").append(avgLat).append(",").append(avgLon);
        sb.append("&zoom=").append(zoom);

        sb.append("&markers=")
                .append(route.get(0).get(0)).append(",")
                .append(route.get(0).get(1)).append(",blue1|")
                .append(route.get(route.size()-1).get(0)).append(",")
                .append(route.get(route.size()-1).get(1)).append(",red1");

        sb.append("&path=enc:").append(encoded);

        return sb.toString();
    }



}