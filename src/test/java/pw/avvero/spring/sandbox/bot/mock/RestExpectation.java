package pw.avvero.spring.sandbox.bot.mock;

import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import org.springframework.web.client.RestTemplate;
import pw.avvero.test.http.RequestCaptor;

public class RestExpectation extends RestExpectationMockRestServiceServer {

    public final OpenaiMock openai;
    public final TelegramMock telegram;

    public RestExpectation(RestTemplate restTemplate) {
        super(MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build());
        this.openai = new OpenaiMock() {
            @Override
            public RequestCaptor completions(ResponseCreator responseCreator) {
                return map("https://api.openai.com/v1/chat/completions", responseCreator);
            }
        };
        this.telegram = new TelegramMock() {
            @Override
            public RequestCaptor sendMessage(ResponseCreator responseCreator) {
                return map("https://api.telegram.org/sendMessage", responseCreator);
            }
        };
    }

    public void reset() {
        mockServer.reset();
    }
}
