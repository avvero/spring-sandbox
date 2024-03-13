package pw.avvero.test.http;

import groovy.json.JsonSlurper;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.test.web.client.RequestMatcher;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class RequestCaptor implements RequestMatcher {

    private final AtomicInteger times = new AtomicInteger(0);
    private String bodyString;
    private Object body;
    private HttpHeaders headers = new HttpHeaders();

    public void match(ClientHttpRequest request) throws AssertionError {
        this.times.incrementAndGet();
        MockClientHttpRequest mockRequest = (MockClientHttpRequest) request;
        this.bodyString = mockRequest.getBodyAsString();
        if (!this.bodyString.isEmpty()) {
            this.body = (new JsonSlurper()).parseText(mockRequest.getBodyAsString());
        }
        this.headers = mockRequest.getHeaders();
    }

    public int getTimes() {
        return times.get();
    }

    public void clear() {
        this.times.set(0);
        this.body = null;
        this.headers = null;
    }
}
