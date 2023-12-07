package pw.avvero.spring.sandbox.weather

import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.web.client.RestTemplate
import pw.avvero.spring.sandbox.ContainersConfiguration
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.client.ExpectedCount.once
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = [ContainersConfiguration])
class B1GetForecastTests extends Specification {

    @Autowired
    WeatherService weatherService
    @Autowired
    RestTemplate restTemplate
    @Shared
    MockRestServiceServer mockServer;

    def setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
    }

    def "Forecast for provided city London is 42"() {
        setup:
        def requestCaptor = new RequestCaptor()
        mockServer.expect(once(), requestTo("https://external-weather-api.com"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(requestCaptor)
                .andRespond(withSuccess('{"result": "42"}', MediaType.APPLICATION_JSON));
        when:
        def forecast = weatherService.getForecast("London")
        then:
        forecast == "42"
        requestCaptor.times == 1
        requestCaptor.entity.city == "London"
    }

    def "Forecast for provided city Unknown is 42"() {
        setup:
        def requestCaptor = new RequestCaptor()
        mockServer.expect(once(), requestTo("https://external-weather-api.com"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(requestCaptor)
                .andRespond(withSuccess('{"result": "42"}', MediaType.APPLICATION_JSON));
        when:
        def forecast = weatherService.getForecast("Unknown")
        then:
        forecast == "42"
        requestCaptor.times == 1
        requestCaptor.entity.city == "London"
    }

}
