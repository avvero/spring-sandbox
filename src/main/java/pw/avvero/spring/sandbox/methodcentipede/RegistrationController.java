package pw.avvero.spring.sandbox.methodcentipede;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/registration/registerClient")
    public void getForecast(@RequestBody RegistrationRequest request) {
        registrationService.registerClient(request);
    }

    public record RegistrationRequest(String email,
                                      String firstName,
                                      String lastName) {
    }
}
