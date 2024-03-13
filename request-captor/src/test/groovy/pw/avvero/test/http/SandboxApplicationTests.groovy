package pw.avvero.test.http

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

@SpringBootTest
@ActiveProfiles(profiles = "test")
class SandboxApplicationTests extends Specification {

    def "Context loads"() {
        expect:
        1 == 1
    }

}
