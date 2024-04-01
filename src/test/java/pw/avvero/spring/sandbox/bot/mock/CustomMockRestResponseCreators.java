package pw.avvero.spring.sandbox.bot.mock;

import au.com.dius.pact.core.model.DefaultPactReader;
import au.com.dius.pact.core.model.RequestResponseInteraction;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.Response;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.intellij.lang.annotations.Language;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.util.Assert;
import org.springframework.web.client.ResourceAccessException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RequiredArgsConstructor
public class CustomMockRestResponseCreators extends MockRestResponseCreators {

    public static ResponseCreator withSuccess(@Language("JSON") String body) {
        return MockRestResponseCreators.withSuccess(body, APPLICATION_JSON);
    }

    public static ResponseCreator withResourceAccessException() {
        return (request) -> {
            throw new ResourceAccessException("Error");
        };
    }

    public static ResponseCreator fromContract(String contractFileName) {
        File pactFile = new File("src/test/resources/contracts/" + contractFileName);
        RequestResponsePact pact = (RequestResponsePact) DefaultPactReader.INSTANCE.loadPact(pactFile);
        Assert.isTrue(pact.getInteractions().size() == 1, "There should be exactly one iteration per contract file");
        RequestResponseInteraction interaction = (RequestResponseInteraction) pact.getInteractions().get(0);
        Response response = interaction.getResponse();
        return MockRestResponseCreators.withRawStatus(response.getStatus())
                .contentType(MediaType.parseMediaType(Objects.requireNonNull(response.determineContentType().asString())))
                .body(response.getBody().valueAsString());
    }

    public static String fromFile(String testResourceFile) throws IOException {
        return fromFile(testResourceFile, Map.of());
    }

    public static String fromFile(String file, Map<String, Object> values) throws IOException {
        String text = IOGroovyMethods.getText(ResourceGroovyMethods.newReader(new File("src/test/resources/" + file)));
        return new StringSubstitutor(values).replace(text);
    }
}
