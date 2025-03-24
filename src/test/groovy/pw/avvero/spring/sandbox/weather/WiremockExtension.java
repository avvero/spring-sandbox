package pw.avvero.spring.sandbox.weather;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.intellij.lang.annotations.Language;
import org.springframework.http.HttpStatus;
import pw.avvero.test.http.WiredRequestCaptor;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class WiremockExtension {

    private final WireMockServer mockServer;

    public WiremockExtension(WireMockServer mockServer) {
        this.mockServer = mockServer;
    }

    public WiredRequestCaptor forecast(ResponseDefinitionBuilder responseDefinitionBuilder) {
        StubMapping forecastMapping = mockServer.stubFor(post(urlEqualTo("/forecast"))
                .willReturn(responseDefinitionBuilder));
        return new WiredRequestCaptor(mockServer, forecastMapping);
    }

    public static ResponseDefinitionBuilder withSuccess(@Language("JSON") String body) {
        return WireMock.aResponse()
                .withBody(body)
                .withStatus(200)
                .withHeader("Content-Type", "application/json");
    }

    public static ResponseDefinitionBuilder withDelay(Duration delay, ResponseDefinitionBuilder responseDefinitionBuilder) {
        return responseDefinitionBuilder.withFixedDelay((int) delay.toMillis());
    }

    public static ResponseDefinitionBuilder withStatus(HttpStatus httpStatus, String body) {
        return WireMock.aResponse().withBody(body).withStatus(httpStatus.value());
    }

    public static ResponseDefinitionBuilder withConnectionReset() {
        return WireMock.aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER);
    }
}
