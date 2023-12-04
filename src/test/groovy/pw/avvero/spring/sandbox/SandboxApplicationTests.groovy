package pw.avvero.spring.sandbox

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import pw.avvero.spring.sandbox.ContainersConfiguration
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = [ContainersConfiguration])
class SandboxApplicationTests extends Specification {

    def "Context loads"() {
        expect:
        1 == 1
    }

}
