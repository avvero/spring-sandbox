package pw.avvero.spring.sandbox.bot.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@Data
@RequiredArgsConstructor
public class RestExpectation {

    private final WireMockServer wireMockServer;
    public final OpenaiMock openai;
    public final TelegramMock telegram;

    public RestExpectation(int port) {
        this.wireMockServer = new WireMockServer(WireMockConfiguration.options().port(port));
        this.wireMockServer.start();
        this.openai = new OpenaiMock(wireMockServer);
        this.telegram = new TelegramMock(wireMockServer);
    }

    public void stop() {
        wireMockServer.stop();
    }
}
