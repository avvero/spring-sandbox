package pw.avvero.spring.sandbox.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final String url;

    public WeatherService(RestTemplate restTemplate,
                          @Value("${app.weather.url}") String url) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    public String getForecast(String city) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> request = Map.of("city", city);
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(request, headers);
        return restTemplate.postForObject(url, requestEntity, String.class);
    }
}
