package pw.avvero.spring.sandbox.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RequiredArgsConstructor
public class RestExpectationResponseCreator implements ResponseCreator {

    private final DefaultResponseCreator delegate;

    @Override
    public ClientHttpResponse createResponse(ClientHttpRequest request) throws IOException {
        return delegate.createResponse(request);
    }

    public static RestExpectationResponseCreator withSuccess(String body) {
        return new RestExpectationResponseCreator(MockRestResponseCreators.withSuccess(body, APPLICATION_JSON));
    }
}
