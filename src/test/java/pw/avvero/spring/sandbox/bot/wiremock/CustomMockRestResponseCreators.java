package pw.avvero.spring.sandbox.bot.wiremock;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import lombok.RequiredArgsConstructor;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@RequiredArgsConstructor
public class CustomMockRestResponseCreators {

    public static ResponseDefinitionBuilder withSuccess(String body) {
        return aResponse()
                .withBody(body)
                .withStatus(200)
                .withHeader("Content-Type", "application/json");
    }
}
