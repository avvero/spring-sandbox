package pw.avvero.spring.sandbox;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {ContainersConfiguration.class})
class SandboxApplicationJUnitTests {

    @Test
    void contextLoads() {
    }

}
