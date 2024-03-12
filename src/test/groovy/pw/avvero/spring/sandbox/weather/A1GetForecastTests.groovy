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
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.client.ExpectedCount.once
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = [ContainersConfiguration])
class A1GetForecastTests extends Specification {

    @Autowired
    WeatherService weatherService
    @Autowired
    RestTemplate restTemplate
    @Shared
    MockRestServiceServer mockServer

    def setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
    }

    def cleanup() {
        mockServer.reset()
    }

    def "Forecast for provided city London is 42"() {
        setup:          // (1)
        mockServer.expect(once(), requestTo("https://external-weather-api.com/forecast")) // (2)
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath('$.city', Matchers.equalTo("London")))                // (3)
                .andRespond(withSuccess('{"result": "42"}', MediaType.APPLICATION_JSON)); // (4)
        when:          // (5)
        def forecast = weatherService.getForecast("London")
        then:          // (6)
        forecast == "42"     // (7)
        mockServer.verify()  // (8)
    }

    @Ignore
    def "Incorrect city in request"() {
        setup:
        mockServer.expect(once(), requestTo("https://external-weather-api.com/forecast"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath('$.city', Matchers.equalTo("London")))                // (1)
                .andRespond(withSuccess('{"result": "42"}', MediaType.APPLICATION_JSON));
        when:
        def forecast = weatherService.getForecast("Unknown")                              // (2)
        then:
        forecast == "42"
        mockServer.verify()
    }

    @Ignore
    def "Incorrect uri for mock"() {
        setup:
        mockServer.expect(once(), requestTo("https://foo.com/forecast"))                  // (1)
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath('$.city', Matchers.equalTo("London")))
                .andRespond(withSuccess('{"result": "42"}', MediaType.APPLICATION_JSON));
        when:
        def forecast = weatherService.getForecast("London")
        then:
        forecast == "42"
        mockServer.verify()
    }

}
