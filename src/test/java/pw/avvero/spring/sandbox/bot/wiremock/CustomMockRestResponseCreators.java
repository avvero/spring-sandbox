package pw.avvero.spring.sandbox.bot.wiremock;

import au.com.dius.pact.core.model.DefaultPactReader;
import au.com.dius.pact.core.model.RequestResponseInteraction;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.Response;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.intellij.lang.annotations.Language;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@RequiredArgsConstructor
public class CustomMockRestResponseCreators {

    public static ResponseDefinitionBuilder withSuccess(@Language("JSON") String body) {
        return aResponse()
                .withBody(body)
                .withStatus(200)
                .withHeader("Content-Type", "application/json");
    }

    public static ResponseDefinitionBuilder withStatus(HttpStatus httpStatus) {
        return aResponse()
                .withStatus(httpStatus.value());
    }

    public static ResponseDefinitionBuilder fromContract(String contractFileName) {
        File pactFile = new File("src/test/resources/contracts/" + contractFileName);
        RequestResponsePact pact = (RequestResponsePact) DefaultPactReader.INSTANCE.loadPact(pactFile);
        Assert.isTrue(pact.getInteractions().size() == 1, "There should be exactly one iteration per contract file");
        RequestResponseInteraction interaction = (RequestResponseInteraction) pact.getInteractions().get(0);
        Response response = interaction.getResponse();
        return aResponse()
                .withBody(response.getBody().valueAsString())
                .withStatus(response.getStatus())
                .withHeader("Content-Type", response.determineContentType().asString());
    }

    public static String fromFile(String testResourceFile) throws IOException {
        return fromFile(testResourceFile, Map.of());
    }

    public static String fromFile(String file, Map<String, Object> values) throws IOException {
        String text = IOGroovyMethods.getText(ResourceGroovyMethods.newReader(new File("src/test/resources/" + file)));
        return new StringSubstitutor(values).replace(text);
    }
}
