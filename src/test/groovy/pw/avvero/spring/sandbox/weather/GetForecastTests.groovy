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

import static org.springframework.test.web.client.ExpectedCount.manyTimes
import static org.springframework.test.web.client.ExpectedCount.once
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = [ContainersConfiguration])
class GetForecastTests extends Specification {

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
        mockServer.expect(once(), requestTo("https://external-weather-api.com"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath('$.city', Matchers.equalTo("London")))
                .andRespond(withSuccess('{"result": 42}', MediaType.APPLICATION_JSON));
        when:
        weatherService.getForecast("London")
        then:
        mockServer.verify()
    }

    def "Forecast for provided city Unknown is 42"() {
        setup:
        mockServer.expect(once(), requestTo("https://external-weather-api.com"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath('$.city', Matchers.equalTo("London")))
                .andRespond(withSuccess('{"result": 42}', MediaType.APPLICATION_JSON));
        when:
        weatherService.getForecast("Unknown")
        then:
        mockServer.verify()
    }

}
