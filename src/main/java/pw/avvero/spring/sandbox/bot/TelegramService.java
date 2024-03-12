package pw.avvero.spring.sandbox.bot;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TelegramService {
    private final RestTemplate restTemplate;
    private final String url;

    public TelegramService(RestTemplate restTemplate,
                           @Value("${telegram.uri}") String url) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    public void sendMessage(String chatId, String replyToMessageId, String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        SendMessageRequest request = new SendMessageRequest(chatId, replyToMessageId, text);
        HttpEntity<SendMessageRequest> requestEntity = new HttpEntity<>(request, headers);
        restTemplate.postForObject(url + "/sendMessage", requestEntity, Object.class);
    }

    public record SendMessageRequest(@JsonProperty("chat_id") String chatId,
                                     @JsonProperty("reply_to_message_id") String replyToMessageId,
                                     String text) {
    }
}
