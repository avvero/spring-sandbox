package pw.avvero.spring.sandbox.bot.mock


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate
import pw.avvero.spring.sandbox.ContainersConfiguration
import pw.avvero.test.http.RequestCaptor
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.client.ExpectedCount.manyTimes
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = [ContainersConfiguration])
@AutoConfigureMockMvc
class FeatureGTests extends Specification {

    @Autowired
    RestTemplate restTemplate
    @Autowired
    MockMvc mockMvc
    @Shared
    MockRestServiceServer mockServer

    def setup() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build()
    }

    def cleanup() {
        mockServer.reset()
    }

    def "User Message Processing with OpenAI"() {
        setup:
        def openaiRequestCaptor = new RequestCaptor()
        mockServer.expect(manyTimes(), requestTo("https://api.openai.com/v1/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(openaiRequestCaptor)
                .andRespond(withSuccess("""{
                    "id": "chatcmpl-123",
                    "object": "chat.completion",
                    "created": 1677652288,
                    "model": "gpt-3.5-turbo-0125",
                    "system_fingerprint": "fp_44709d6fcb",
                    "choices": [{
                        "index": 0,
                        "message": {
                            "role": "assistant",
                            "content": "Hello there, how may I assist you today?"
                        },
                        "logprobs": null,
                        "finish_reason": "stop"
                    }],
                    "usage": {
                        "prompt_tokens": 9,
                        "completion_tokens": 12,
                        "total_tokens": 21
                    }
                }""", MediaType.APPLICATION_JSON))
        def telegramRequestCaptor = new RequestCaptor()
        mockServer.expect(manyTimes(), requestTo("https://api.telegram.org/sendMessage"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(telegramRequestCaptor)
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON))
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
        openaiRequestCaptor.body.model == "gpt-3.5-turbo"
        openaiRequestCaptor.body.messages.size() == 1
        openaiRequestCaptor.body.messages[0].role == "user"
        openaiRequestCaptor.body.messages[0].content == "Hello!"
        and:
        telegramRequestCaptor.times == 1
        telegramRequestCaptor.body.chat_id == "200000"
        telegramRequestCaptor.body.reply_to_message_id == "100000"
        telegramRequestCaptor.body.text == "Hello there, how may I assist you today?"
    }
}