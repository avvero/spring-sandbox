package pw.avvero.spring.sandbox.bot.mock;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@Data
@RequiredArgsConstructor
public class RestExpectation {

    private final MockRestServiceServer mockServer;
    public final OpenaiMock openai;
    public final TelegramMock telegram;

    public RestExpectation(RestTemplate restTemplate) {
        this.mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
        this.openai = new OpenaiMock(mockServer);
        this.telegram = new TelegramMock(mockServer);
    }

    public void reset() {
        mockServer.reset();
    }
}
