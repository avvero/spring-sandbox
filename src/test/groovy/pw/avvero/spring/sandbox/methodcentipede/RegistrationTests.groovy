package pw.avvero.spring.sandbox.methodcentipede

import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import pw.avvero.spring.sandbox.ContainersConfiguration
import pw.avvero.spring.sandbox.IdGenerator
import pw.avvero.spring.sandbox.RecordCaptorConfiguration
import pw.avvero.spring.sandbox.KafkaContainerConfiguration
import pw.avvero.test.kafka.KafkaSupport
import pw.avvero.test.kafka.RecordCaptor
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = [ContainersConfiguration, KafkaContainerConfiguration, RecordCaptorConfiguration])
@AutoConfigureMockMvc
class RegistrationTests extends Specification {

    @Autowired
    MockMvc mockMvc
    @Autowired
    ApplicationContext applicationContext
    @Autowired
    RecordCaptor recordCaptor

    def "Client is registered with API"() {
        setup:
        KafkaSupport.waitForPartitionAssignment(applicationContext)
        def id = IdGenerator.getNext()
        when:
        mockMvc.perform(post("/registration/registerClient")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"email": "client${id}@mail.ru", "firstName": "Ivan", "lastName": "Ivanov"}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
        KafkaSupport.waitForPartitionOffsetCommit(applicationContext)
        and:
        def record = recordCaptor.getRecords("topic-registration", "client${id}@mail.ru".toString()).first()
        then:
        JSONAssert.assertEquals("""{
            "clientId": 1,
            "email": "client1@mail.ru",
            "firstName": "Ivan",
            "lastName": "Ivanov"
        }""", (String) record.value, false)
    }

}
