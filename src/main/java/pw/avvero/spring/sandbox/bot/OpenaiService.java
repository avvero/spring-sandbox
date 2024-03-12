package pw.avvero.spring.sandbox.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OpenaiService {
    private final RestTemplate restTemplate;
    private final String url;

    public OpenaiService(RestTemplate restTemplate,
                         @Value("${openai.uri}") String url) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    public String process(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        CompletionRequest request = new CompletionRequest(
                "gpt-3.5-turbo",
                List.of(new CompletionMessage("user", content))
        );
        HttpEntity<CompletionRequest> requestEntity = new HttpEntity<>(request, headers);
        CompletionResponse response = restTemplate.postForObject(url + "/v1/chat/completions", requestEntity,
                CompletionResponse.class);
        return response.choices.get(0).message.content;
    }

    public record CompletionRequest(String model, List<CompletionMessage> messages) {
    }

    public record CompletionMessage(String role, String content) {
    }

    public record CompletionResponse(List<CompletionChoice> choices) {
    }

    public record CompletionChoice(Message message) {}
    public record Message(String content) {}
}
