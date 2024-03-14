package pw.avvero.spring.sandbox.bot;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import pw.avvero.test.http.RequestCaptor;

import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@Data
@RequiredArgsConstructor
public class RestExpectation {

    private final MockRestServiceServer mockServer;

    public RestExpectation(RestTemplate restTemplate) {
        this.mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
    }

    public interface OpenaiApi {
        RequestCaptor completions(RestExpectationResponseCreator responseCreator);
    }

    public OpenaiApi openai = new OpenaiApi() {
        public RequestCaptor completions(RestExpectationResponseCreator responseCreator) {
            RequestCaptor requestCaptor = new RequestCaptor();
            mockServer.expect(manyTimes(), requestTo("https://api.openai.com/v1/chat/completions"))
                    .andExpect(method(HttpMethod.POST))
                    .andExpect(requestCaptor)
                    .andRespond(responseCreator);
            return requestCaptor;
        }
    };

    public interface TelegramApi {
        RequestCaptor sendMessage(RestExpectationResponseCreator responseCreator);
    }

    public TelegramApi telegram = new TelegramApi() {
        @Override
        public RequestCaptor sendMessage(RestExpectationResponseCreator responseCreator) {
            RequestCaptor requestCaptor = new RequestCaptor();
            mockServer.expect(manyTimes(), requestTo("https://api.telegram.org/sendMessage"))
                    .andExpect(method(HttpMethod.POST))
                    .andExpect(requestCaptor)
                    .andRespond(responseCreator);
            return requestCaptor;
        }
    };

    public void reset() {
        mockServer.reset();
    }

}
