package pw.avvero.spring.sandbox.bot.mock;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import pw.avvero.test.http.RequestCaptor;

import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@RequiredArgsConstructor
public class TelegramMock {
    private final MockRestServiceServer mockServer;

    public RequestCaptor sendMessage(DefaultResponseCreator responseCreator) {
        RequestCaptor requestCaptor = new RequestCaptor();
        mockServer.expect(manyTimes(), requestTo("https://api.telegram.org/sendMessage"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(requestCaptor)
                .andRespond(responseCreator);
        return requestCaptor;
    }
}
