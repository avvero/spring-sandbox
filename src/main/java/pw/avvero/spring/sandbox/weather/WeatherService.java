package pw.avvero.spring.sandbox.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;

@Service
public class WeatherService {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherRequest implements Serializable {
        private String city;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeatherResponse implements Serializable {
        private String result;
    }

    private final RestTemplate restTemplate;
    private final String url;

    public WeatherService(RestTemplate restTemplate,
                          @Value("${app.weather.url}") String url) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    /**
     * Retrieves the weather forecast for the specified city.
     * <br><img src="classpath:diagram/forecast.png" alt="Forecast integration">
     *
     * @param city The name of the city for which the forecast is requested.
     * @return A string representing the weather forecast for the specified city.
     */
    public String getForecast(String city) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        WeatherRequest request = new WeatherRequest(city);
        HttpEntity<WeatherRequest> requestEntity = new HttpEntity<>(request, headers);
        WeatherResponse response = restTemplate.postForObject(url, requestEntity, WeatherResponse.class);
        return response.result;
    }
}
