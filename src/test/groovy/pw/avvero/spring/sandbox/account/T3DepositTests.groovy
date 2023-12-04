package pw.avvero.spring.sandbox.account

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import pw.avvero.spring.sandbox.ContainersConfiguration
import spock.lang.Specification

import static org.hamcrest.Matchers.is
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pw.avvero.spring.sandbox.account.TestUtils.json

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ContextConfiguration(classes = [ContainersConfiguration])
@AutoConfigureMockMvc
class T3DepositTests extends Specification {

    @Autowired
    MockMvc mockMvc

    def "Deposit method returns NotFound code if account is not found by id"() {
        expect:
        mockMvc.perform(post("/api/v1/account/deposit")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": 0, "amount": "100"}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.resultCode', is("NotFound")))
    }

    def "Account could be replenished with deposit method"() {
        when: "Create account"
        def createAccountResponse = mockMvc.perform(post("/api/v1/account/create")
                .contentType(APPLICATION_JSON_VALUE)
                .content()
                .accept(APPLICATION_JSON_VALUE))
                .andReturn().response
        def accountId = json(createAccountResponse.contentAsString).id as Integer
        then: "Check info"
        mockMvc.perform(post("/api/v1/account/info")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath('$.balance', is(0)))
        expect: "Replenish on 100"
        mockMvc.perform(post("/api/v1/account/deposit")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId, "amount": "100"}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.resultCode', is("Ok")))
        and: "Balance is changed"
        mockMvc.perform(post("/api/v1/account/info")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.balance', is(100)))
        and: "Replenish on 50"
        mockMvc.perform(post("/api/v1/account/deposit")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId, "amount": "50"}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.resultCode', is("Ok")))
        and: "Balance is changed"
        mockMvc.perform(post("/api/v1/account/info")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.balance', is(150)))
    }
}
