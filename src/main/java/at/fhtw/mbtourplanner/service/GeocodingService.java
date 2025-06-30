package at.fhtw.mbtourplanner.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeocodingService {
    private final WebClient webClient = WebClient.create("https://nominatim.openstreetmap.org");
    public double[] geocode(String address) {
        var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("format","json")
                        .queryParam("limit","1")
                        .queryParam("q", address)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode[].class)
                .block();

        JsonNode first = response[0];
        return new double[]{
                first.get("lat").asDouble(),
                first.get("lon").asDouble()
        };
    }
}
