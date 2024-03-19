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
public abstract class RestExpectationMockRestServiceServer {

    protected final MockRestServiceServer mockServer;

    protected RequestCaptor map(String uri, DefaultResponseCreator responseCreator) {
        RequestCaptor requestCaptor = new RequestCaptor();
        mockServer.expect(manyTimes(), requestTo(uri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(requestCaptor)
                .andRespond(responseCreator);
        return requestCaptor;
    }

}
