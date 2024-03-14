package pw.avvero.spring.sandbox.bot.mock;

import lombok.RequiredArgsConstructor;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RequiredArgsConstructor
public class CustomMockRestResponseCreators extends MockRestResponseCreators {

    public static DefaultResponseCreator withSuccess(String body) {
        return MockRestResponseCreators.withSuccess(body, APPLICATION_JSON);
    }
}
