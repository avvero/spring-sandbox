package pw.avvero.spring.sandbox.bot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    public record TelegramWebhookMessage(Message message) {
    }

    public record Message(@JsonProperty("message_id") String id,
                          Chat chat,
                          String text) {
    }

    public record Chat(String id) {
    }

}
