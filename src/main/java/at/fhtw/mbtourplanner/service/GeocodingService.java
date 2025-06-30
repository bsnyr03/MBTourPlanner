package at.fhtw.mbtourplanner.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

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
                .bodyToMono(new ParameterizedTypeReference<List<Map<String,Object>>>(){})
                .block();
        if (response == null || response.isEmpty()) {
            throw new RuntimeException("Geocoding failed for " + address);
        }
        Map<String,Object> first = response.get(0);
        return new double[]{
                Double.parseDouble((String) first.get("lat")),
                Double.parseDouble((String) first.get("lon"))
        };
    }
}
