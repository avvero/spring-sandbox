package pw.avvero.spring.sandbox.weather;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import groovy.json.JsonSlurper;
import org.awaitility.Awaitility;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.like;
import static java.util.stream.Collectors.toMap;


public class WiredRequestCaptor {

    private final WireMockServer wireMockServer;
    private final StubMapping mapping;

    public WiredRequestCaptor(WireMockServer wireMockServer, StubMapping mapping) {
        this.wireMockServer = wireMockServer;
        this.mapping = mapping;
    }

    private List<LoggedRequest> findAll() {
        return wireMockServer.findAll(like(mapping.getRequest()));
    }

    public String getBodyString() {
        Awaitility.await()
                .alias("Request: " + null)
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(10, TimeUnit.MILLISECONDS)
                .until(() -> !findAll().isEmpty());

        Assert.state(findAll().size() == 1, "There are more than 1 requests");
        return findAll().get(0).getBodyAsString();
    }

    public <T> T getBody(Class<T> ignore) {
        throw new UnsupportedOperationException();
    }

    public Object getBody() {
        return (new JsonSlurper()).parseText(getBodyString());
    }

    public HttpHeaders getHeaders() {
        return new HttpHeaders(wireMockServer.findAll(like(mapping.getRequest())).get(0).getHeaders().all().stream().collect(toMap(h -> h.caseInsensitiveKey().value(), HttpHeader::values, (a, b) -> b, HttpHeaders::new)));
    }

    public int getTimes() {
        return wireMockServer.findAll(like(mapping.getRequest())).size();
    }

    public String getUrl() {
        return mapping.getRequest().getUrl();
    }
}
