package train.booking.train.booking.service;//package train.booking.train.booking.service;
//
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import train.booking.train.booking.dto.DistanceRequestDTO;
import train.booking.train.booking.model.DistanceCoordinates;
import train.booking.train.booking.model.Station;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DistanceCalculatorService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    @Value("${api.baseurl}")
    private String baseUrl;

    @Value("${api.key}")
    private String apiKey;
public String calculateDistance(Station departure, Station arrival) {
    String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/v2/directions/driving-car")
            .queryParam("api_key", apiKey)
            .toUriString();

    DistanceRequestDTO requestBody = new DistanceRequestDTO(
            new double[][]{
                    {departure.getLongitude(), departure.getLatitude()},
                    {arrival.getLongitude(), arrival.getLatitude()}
            },
            "km"
    );

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<DistanceRequestDTO> request = new HttpEntity<>(requestBody, headers);

    try {
        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

        if (response != null && response.containsKey("routes")) {
            List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
            if (!routes.isEmpty()) {
                Map<String, Object> route = routes.get(0);
                Map<String, Object> summary = (Map<String, Object>) route.get("summary");
                double distance = (double) summary.get("distance");
                return String.format("%.2f km", distance);
            }
        }
    } catch (Exception e) {
        throw new RuntimeException("Error calling distance API: " + e.getMessage(), e);
    }

    throw new RuntimeException("Unable to calculate distance");
}
}