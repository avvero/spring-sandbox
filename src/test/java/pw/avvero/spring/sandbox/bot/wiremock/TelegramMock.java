package pw.avvero.spring.sandbox.bot.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import pw.avvero.test.http.RequestCaptor;
import pw.avvero.test.http.WiredRequestCaptor;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@RequiredArgsConstructor
public class TelegramMock {
    private final WireMockServer wireMockServer;

    public WiredRequestCaptor sendMessage(ResponseDefinitionBuilder responseDefinitionBuilder) {
        StubMapping completionsMapping = wireMockServer
                .stubFor(post(urlEqualTo("/sendMessage"))
                        .willReturn(responseDefinitionBuilder));
        return new WiredRequestCaptor(wireMockServer, completionsMapping);
    }
}
