package pw.avvero.spring.sandbox.bot.mock;

import lombok.RequiredArgsConstructor;
import org.codehaus.groovy.runtime.IOGroovyMethods;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.intellij.lang.annotations.Language;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.io.File;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@RequiredArgsConstructor
public class CustomMockRestResponseCreators extends MockRestResponseCreators {

    public static DefaultResponseCreator withSuccess(@Language("JSON") String body) {
        return MockRestResponseCreators.withSuccess(body, APPLICATION_JSON);
    }

    public static String fromFile(String testResourceFile) throws IOException {
        return IOGroovyMethods.getText(ResourceGroovyMethods.newReader(new File("src/test/resources/" + testResourceFile)));
    }
}
