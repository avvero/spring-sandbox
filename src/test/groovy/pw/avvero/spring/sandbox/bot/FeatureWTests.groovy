package pw.avvero.spring.sandbox.bot

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate
import pw.avvero.spring.sandbox.ContainersConfiguration
import pw.avvero.test.http.WiredRequestCaptor
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = [ContainersConfiguration])
@AutoConfigureMockMvc
@TestPropertySource(properties = [
        "telegram.uri=http://localhost:10080",
        "openai.uri=http://localhost:10080"
])
@DirtiesContext
class FeatureWTests extends Specification {

    @Autowired
    RestTemplate restTemplate
    @Autowired
    ApplicationContext applicationContext
    @Autowired
    MockMvc mockMvc
    @Shared
    WireMockServer wireMockServer

    def setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options()
                .port(10080))
        wireMockServer.start()
    }

    def cleanup() {
        wireMockServer.stop()
    }

    def "User Message Processing with OpenAI"() {
        setup:
        StubMapping completionsMapping = wireMockServer.stubFor(WireMock.post(urlEqualTo("/v1/chat/completions"))
                .willReturn(aResponse()
                        .withBody("""{
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
                        }""")
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")))
        def openaiRequestCaptor = new WiredRequestCaptor(wireMockServer, completionsMapping)
        StubMapping sendMessageMapping = wireMockServer.stubFor(WireMock.post(urlEqualTo("/sendMessage"))
                .willReturn(aResponse()
                        .withBody('{}')
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")))
        def telegramRequestCaptor = new WiredRequestCaptor(wireMockServer, sendMessageMapping)
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
        JSONAssert.assertEquals("""{
            "model": "gpt-3.5-turbo",
            "messages": [
                {
                    "role": "user",
                    "content": "Hello!"
                }
            ]
        }""", openaiRequestCaptor.bodyString, false)
        and:
        telegramRequestCaptor.times == 1
        JSONAssert.assertEquals("""{
            "chat_id": "200000",
            "reply_to_message_id": "100000",
            "text": "Hello there, how may I assist you today?"
        }""", telegramRequestCaptor.bodyString, false)
    }
}
