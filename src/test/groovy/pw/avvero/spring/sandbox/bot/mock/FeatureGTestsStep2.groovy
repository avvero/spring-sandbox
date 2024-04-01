package pw.avvero.spring.sandbox.bot.mock


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate
import pw.avvero.spring.sandbox.ContainersConfiguration
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pw.avvero.spring.sandbox.bot.mock.CustomMockRestResponseCreators.fromFile
import static pw.avvero.spring.sandbox.bot.mock.CustomMockRestResponseCreators.withSuccess

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = [ContainersConfiguration])
@AutoConfigureMockMvc
class FeatureGTestsStep2 extends Specification {

    @Autowired
    RestTemplate restTemplate
    @Autowired
    MockMvc mockMvc
    @Shared
    RestExpectation restExpectation

    def setup() {
        restExpectation = new RestExpectation(restTemplate)
    }

    def cleanup() {
        restExpectation.cleanup()
    }

    def "User Message Processing with OpenAI"() {
        setup:
        def openaiRequestCaptor = restExpectation.openai.completions(withSuccess(fromFile("json/openai/response.json")))
        def telegramRequestCaptor = restExpectation.telegram.sendMessage(withSuccess("{}"))
        when:
        mockMvc.perform(post("/telegram/webhook")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{
                    "update_id": 134078166,
                    "message": {
                        "message_id": 100000,
                        "from": {
                            "id": 50000,
                            "is_bot": false,
                            "first_name": "Ivav",
                            "last_name": "Ivanov",
                            "username": "ivan",
                            "language_code": "ru"
                        },
                        "chat": {
                            "id": 200000,
                            "title": "Bot"
                        },
                        "date": 1710132752,
                        "text": "Hello!"
                    }
                }""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
        then:
        openaiRequestCaptor.times == 1
        assertEquals("""{
            "model": "gpt-3.5-turbo",
            "messages": [{
                "role": "user",
                "content": "Hello!"
            }]
        }""", openaiRequestCaptor.bodyString, false)
        and:
        telegramRequestCaptor.times == 1
        assertEquals("""{
            "chat_id": "200000",
            "reply_to_message_id": "100000",
            "text": "Hello there, how may I assist you today?"
        }""", telegramRequestCaptor.bodyString, false)
    }

    @Unroll
    def "User Message Processing with OpenAI response: #openaiResponseContent"() {
        setup:
        def openaiRequestCaptor = restExpectation.openai.completions(withSuccess(fromFile("json/openai/response.json",
                [content: openaiResponseContent])))
        def telegramRequestCaptor = restExpectation.telegram.sendMessage(withSuccess("{}"))
        when:
        mockMvc.perform(post("/telegram/webhook")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{
                    "update_id": 134078166,
                    "message": {
                        "message_id": 100000,
                        "from": {
                            "id": 50000,
                            "is_bot": false,
                            "first_name": "Ivav",
                            "last_name": "Ivanov",
                            "username": "ivan",
                            "language_code": "ru"
                        },
                        "chat": {
                            "id": 200000,
                            "title": "Bot"
                        },
                        "date": 1710132752,
                        "text": "Hello!"
                    }
                }""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
        then:
        openaiRequestCaptor.times == 1
        assertEquals("""{
            "model": "gpt-3.5-turbo",
            "messages": [{
                "role": "user",
                "content": "Hello!"
            }]
        }""", openaiRequestCaptor.bodyString, false)
        and:
        telegramRequestCaptor.times == 1
        assertEquals("""{
            "chat_id": "200000",
            "reply_to_message_id": "100000",
            "text": "$openaiResponseContent"
        }""", telegramRequestCaptor.bodyString, false)
        where:
        openaiResponseContent                      | _
        "Hello there, how may I assist you today?" | _
        "Hello! How can I assist you today?"       | _
    }
}