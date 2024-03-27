package pw.avvero.spring.sandbox.bot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor

public class TelegramWebhookController {

    private final OpenaiService openaiService;
    private final TelegramService telegramService;

    @PostMapping("/telegram/webhook")
    public void process(@RequestBody TelegramWebhookMessage request) {
        String openaiResponse = openaiService.process(request.message.text);
        telegramService.sendMessage(request.message.chat.id, request.message.id, openaiResponse);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map response = Map.of(false, "Internal server error");
        return new ResponseEntity<>(response, status);
    }

    public record TelegramWebhookMessage(Message message) {
    }

    public record Message(@JsonProperty("message_id") String id,
                          Chat chat,
                          String text) {
    }

    public record Chat(String id) {
    }

}
