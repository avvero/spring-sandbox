package pw.avvero.spring.sandbox.weather;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @PostMapping("/weather/getForecast")
    public GetForecastResponse getForecast(@RequestBody GetForecastRequest request) {
        String result = weatherService.getForecast(request.city);
        return new GetForecastResponse(result);
    }

    public record GetForecastRequest(String city) {
    }

    public record GetForecastResponse(String result) {
    }
}
