package train.booking.train.booking.service;//package train.booking.train.booking.service;
//
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.util.UriComponentsBuilder;
//import train.booking.train.booking.model.DistanceCoordinates;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class DistanceCalculatorService {
//
//    private final RestTemplate restTemplate;
//
//    @Value("${api.baseUrl}")
//    private String baseUrl;
//
//    @Value("${api.key}")
//    private String apiKey;
//
//    public DistanceCalculatorService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    public String calculateDistance() {
//        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/v2/directions/driving-car")
//                .queryParam("api_key", apiKey)
//                .toUriString();
//
//        Map<String, Object> requestPayload = Map.of(
//                "coordinates", new double[][]{
//                        {DistanceCoordinates.IBADAN_LONGITUDE, DistanceCoordinates.IBADAN_LATITUDE},
//                        {DistanceCoordinates.LAGOS_LONGITUDE, DistanceCoordinates.LAGOS_LATITUDE}
//                },
//                "units", "km"
//        );
//
//        Map<String, Object> response = restTemplate.postForObject(url, requestPayload, Map.class);
//
//        if (response != null && response.containsKey("routes")) {
//            List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
//            if (!routes.isEmpty()) {
//                Map<String, Object> route = routes.get(0);
//                Map<String, Object> summary = (Map<String, Object>) route.get("summary");
//                double distance =(double) summary.get("distance");
//                return distance +  " km";
//            }
//        }
//
//        throw new RuntimeException("Unable to calculate distance");
//    }
//}
//
//
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import train.booking.train.booking.dto.DistanceRequestDTO;
import train.booking.train.booking.model.DistanceCoordinates;

import java.util.List;
import java.util.Map;

@Service
public class DistanceCalculatorService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper; // ✅ Ensure JSON is handled properly

    @Value("${api.baseUrl}")
    private String baseUrl;

    @Value("${api.key}")
    private String apiKey;

    public DistanceCalculatorService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String calculateDistance() {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/v2/directions/driving-car")
                .queryParam("api_key", apiKey)
                .toUriString();

        // ✅ Use DTO to create a proper request body
        DistanceRequestDTO requestBody = new DistanceRequestDTO(
                new double[][]{
                        {DistanceCoordinates.IBADAN_LONGITUDE, DistanceCoordinates.IBADAN_LATITUDE},
                        {DistanceCoordinates.LAGOS_LONGITUDE, DistanceCoordinates.LAGOS_LATITUDE}
                },
                "km"
        );

        // ✅ Set JSON Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<DistanceRequestDTO> request = new HttpEntity<>(requestBody, headers);

        try {
            // ✅ Make API Call
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);

            if (response != null && response.containsKey("routes")) {
                List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
                if (!routes.isEmpty()) {
                    Map<String, Object> route = routes.get(0);
                    Map<String, Object> summary = (Map<String, Object>) route.get("summary");
                    double distance = (double) summary.get("distance");
                    return distance + " km";
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calling distance API: " + e.getMessage(), e);
        }

        throw new RuntimeException("Unable to calculate distance");
    }
}
