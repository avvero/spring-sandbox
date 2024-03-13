package pw.avvero.test.http

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = [RestTemplateConfiguration])
@TestPropertySource(properties = [
        "app.weather.url=http://localhost:10080"
])
@DirtiesContext
class WiredRequestCaptorTests extends Specification {

    @Autowired
    WeatherService weatherService
    @Autowired
    RestTemplate restTemplate
    @Shared
    WireMockServer wireMockServer

    def setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .port(10080))
        wireMockServer.start()
    }

    def cleanup() {
        wireMockServer.stop()
    }

    def "Forecast for provided city London is 42"() {
        setup:
        StubMapping forecastMapping = wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/forecast"))
                .willReturn(WireMock.aResponse()
                        .withBody('{"result": "42"}')
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")))
        def requestCaptor = new WiredRequestCaptor(wireMockServer, forecastMapping)
        when:
        def forecast = weatherService.getForecast("London")
        then:
        forecast == "42"
        requestCaptor.times == 1
        requestCaptor.body.city == "London"
        requestCaptor.headers.get("Content-Type") == ["application/json"]
    }
}
