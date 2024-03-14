package pw.avvero.spring.sandbox.bot.wiremock;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import lombok.RequiredArgsConstructor;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.intellij.lang.annotations.Language;

import java.io.File;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

@RequiredArgsConstructor
public class CustomMockRestResponseCreators {

    public static ResponseDefinitionBuilder withSuccess(@Language("JSON") String body) {
        return aResponse()
                .withBody(body)
                .withStatus(200)
                .withHeader("Content-Type", "application/json");
    }

    public static String fromFile(String testResourceFile) throws IOException {
        return IOGroovyMethods.getText(ResourceGroovyMethods.newReader(new File("src/test/resources/" + testResourceFile)));
    }
}
