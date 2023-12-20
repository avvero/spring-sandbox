package pw.avvero.spring.sandbox.weather

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestTemplate
import pw.avvero.spring.sandbox.ContainersConfiguration
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = [ContainersConfiguration])
@TestPropertySource(properties = [
        "app.weather.url=http://localhost:10080"
])
@DirtiesContext
class A2GetForecastTests extends Specification {

    @Autowired
    WeatherService weatherService
    @Autowired
    RestTemplate restTemplate
    @Shared
    WireMockServer wireMockServer

    def setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .maxRequestJournalEntries(100)
                .port(10080))
        wireMockServer.start()
    }

    def cleanup() {
        wireMockServer.stop()
    }

    def "Forecast for provided city London is 42"() {
        setup:          // (1)
        wireMockServer.stubFor(post(urlEqualTo("/forecast"))                              // (2)
                .willReturn(aResponse()                                                   // (4)
                        .withBody('{"result": "42"}')
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")))
        when:          // (5)
        def forecast = weatherService.getForecast("London")
        then:          // (6)
        forecast == "42"
        wireMockServer.verify(postRequestedFor(urlEqualTo("/forecast"))
                .withRequestBody(matchingJsonPath('$.city', equalTo("London"))))          // (7)
    }

    def "Incorrect city in request"() {
        setup:
        wireMockServer.stubFor(post(urlEqualTo("/forecast"))
                .willReturn(aResponse()
                        .withBody('{"result": "42"}')
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")))
        when:
        def forecast = weatherService.getForecast("Unknown")                              // (1)
        then:
        forecast == "42"
        wireMockServer.verify(postRequestedFor(urlEqualTo("/forecast"))
                .withRequestBody(matchingJsonPath('$.city', equalTo("London"))))
    }

    def "Incorrect uri for mock"() {
        setup:
        wireMockServer.stubFor(post(urlEqualTo("/unknown"))                               // (1)
                .withRequestBody(matchingJsonPath('$.city', equalTo("London")))
                .willReturn(aResponse()
                        .withBody('{"result": "42"}')
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")))
        when:
        def forecast = weatherService.getForecast("London")
        then:
        forecast == "42"
        wireMockServer.verify(postRequestedFor(urlEqualTo("/forecast"))
                .withRequestBody(matchingJsonPath('$.city', equalTo("London"))))
    }

}
