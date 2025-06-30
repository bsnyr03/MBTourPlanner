package at.fhtw.mbtourplanner.service;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import at.fhtw.mbtourplanner.service.PolyLineEncoder;


@Service
@Slf4j
public class OpenRouteService {
    private final WebClient webClient;

    public OpenRouteService(WebClient.Builder builder,
                            @Value("${tours.ors-base-url}") String baseUrl,
                            @Value("${tours.ors-api-key}") String apiKey) {
        this.webClient = builder
                .baseUrl("https://api.openrouteservice.org")
                .defaultHeader("Authorization", apiKey)
                .build();
    }

    public Map<String, Object> getRouteInfo(String profile, List<List<Double>> coords) {
        log.info("ORS request for profile={} coords={}", profile, coords);
        @SuppressWarnings("unchecked")
        Map<String,Object> resp = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/directions/{profile}/geojson")
                        .build(profile))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "coordinates", coords
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (resp == null) {
            log.error("ORS returned null for profile={} coords={}", profile, coords);
            throw new RuntimeException("Empty ORS response");
        }
        @SuppressWarnings("unchecked")
        Object rawFeatures = resp.get("features");
        if (!(rawFeatures instanceof List<?> featuresList) || featuresList.isEmpty()) {
            log.error("Unexpected ORS response structure: {}", resp);
            throw new RuntimeException("Invalid ORS response");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> feature = (Map<String, Object>) featuresList.get(0);

        @SuppressWarnings("unchecked")
        Map<String, Object> properties = (Map<String, Object>) feature.get("properties");
        @SuppressWarnings("unchecked")
        List<?> segments = (List<?>) properties.get("segments");
        if (segments.isEmpty()) {
            log.error("No segments in ORS response: {}", resp);
            throw new RuntimeException("Invalid ORS response");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> segment = (Map<String, Object>) segments.get(0);
        double distance = ((Number) segment.get("distance")).doubleValue();
        double duration = ((Number) segment.get("duration")).doubleValue();
        log.info("ORS result: distance={} meters, duration={} seconds", distance, duration);

        @SuppressWarnings("unchecked")
        Map<String, Object> geometryObj = (Map<String, Object>) feature.get("geometry");
        @SuppressWarnings("unchecked")
        List<List<Double>> geometryCoords = (List<List<Double>>) geometryObj.get("coordinates");

        List<double[]> polyCoords = geometryCoords.stream()
            .map(pt -> new double[]{pt.get(1), pt.get(0)})
            .collect(Collectors.toList());

        String polyline = PolyLineEncoder.encode(polyCoords);

        double sumLat = 0, sumLon = 0;
        for (double[] p : polyCoords) {
            sumLat += p[0];
            sumLon += p[1];
        }
        double centerLat = sumLat / polyCoords.size();
        double centerLon = sumLon / polyCoords.size();

        double[] start = polyCoords.get(0);
        double[] end = polyCoords.get(polyCoords.size() - 1);

        String url = String.format(
            "https://staticmap.openstreetmap.de/staticmap.php?size=600x400" +
            "&center=%f,%f&zoom=13" +
            "&markers=%f,%f,blue1|%f,%f,red1" +
            "&path=enc:%s",
            centerLat, centerLon,
            start[0], start[1],
            end[0], end[1],
            polyline
        );

        log.info("Generated static OSM URL: {}", url);

        return Map.of(
            "distance", distance,
            "duration", duration,
            "staticMapUrl", url
        );
    }

    public String getStaticRouteMapUrl(List<List<Double>> coords, int width, int height, int zoom) {
        double sumLat = 0, sumLon = 0;
        for (List<Double> point : coords) {
            sumLon += point.get(0);
            sumLat += point.get(1);
        }
        double centerLon = sumLon / coords.size();
        double centerLat = sumLat / coords.size();

        List<Double> start = coords.get(0);
        List<Double> end = coords.get(coords.size() - 1);
        String markers = String.format(
                "markers=%f,%f,blue1|%f,%f,red1",
                start.get(1), start.get(0),
                end.get(1), end.get(0)
        );

        StringBuilder pathBuilder = new StringBuilder("path=");
        for (int i = 0; i < coords.size(); i++) {
            List<Double> pt = coords.get(i);
            pathBuilder.append(pt.get(1)).append(",").append(pt.get(0));
            if (i < coords.size() - 1) {
                pathBuilder.append("|");
            }
        }

        String url = String.format(
                "https://staticmap.openstreetmap.de/staticmap.php?" +
                        "size=%dx%d&center=%f,%f&zoom=%d&%s&%s",
                width, height,
                centerLat, centerLon,
                zoom,
                markers,
                pathBuilder.toString()
        );
        log.info("Generated static OSM route map URL: {}", url);
        return url;
    }
}