package pw.avvero.spring.sandbox.methodcentipede;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationServiceFixed {

    private final ClientRepository clientRepository;
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void registerClient(RegistrationController.RegistrationRequest request) {
        var client = clientRepository.save(Client.builder()
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build());
        Message<String> message = mapToEventMessage(client);
        kafkaTemplate.send(message).get();
    }

    private Message<String> mapToEventMessage(Client client) throws JsonProcessingException {
        var event = RegistrationEvent.builder()
                .clientId(client.getId())
                .email(client.getEmail())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .build();
        return MessageBuilder
                .withPayload(objectMapper.writeValueAsString(event))
                .setHeader(KafkaHeaders.TOPIC, "topic-registration")
                .setHeader(KafkaHeaders.KEY, event.email)
                .build();
    }

    @Builder
    public record RegistrationEvent(int clientId, String email, String firstName, String lastName) {}
}
