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
class T4WithdrawalTests extends Specification {

    @Autowired
    MockMvc mockMvc

    def "Deposit method returns NotFound code if account is not found by id"() {
        expect:
        mockMvc.perform(post("/api/v1/account/withdraw")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": 0, "amount": "100"}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.resultCode', is("NotFound")))
    }

    def "Account could be withdrawn with withdraw method"() {
        setup: "Create account"
        def createAccountResponseString = mockMvc.perform(post("/api/v1/account/create")
                .contentType(APPLICATION_JSON_VALUE)
                .content()
                .accept(APPLICATION_JSON_VALUE))
                .andReturn().response.contentAsString
        def createAccountResponse = json(createAccountResponseString)
        def accountId = createAccountResponse.id as Integer
        expect: "Replenish on 200"
        mockMvc.perform(post("/api/v1/account/deposit")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId, "amount": "200"}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
        and: "Check balance"
        mockMvc.perform(post("/api/v1/account/info")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath('$.balance', is(200)))
        and: "Withdrawal on 100"
        mockMvc.perform(post("/api/v1/account/withdraw")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId, "amount": "100"}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.resultCode', is("Ok")))
        and: "Check balance"
        mockMvc.perform(post("/api/v1/account/info")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath('$.balance', is(100)))
        and: "Withdrawal on 50"
        mockMvc.perform(post("/api/v1/account/withdraw")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId, "amount": "50"}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.resultCode', is("Ok")))
        and: "Check balance"
        mockMvc.perform(post("/api/v1/account/info")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath('$.balance', is(50)))
    }

    def "Balance couldn't go below zero"() {
        setup: "Create account"
        def createAccountResponseString = mockMvc.perform(post("/api/v1/account/create")
                .contentType(APPLICATION_JSON_VALUE)
                .content()
                .accept(APPLICATION_JSON_VALUE))
                .andReturn().response.contentAsString
        def accountId = json(createAccountResponseString).id as Integer
        expect: "Replenish on 50"
        mockMvc.perform(post("/api/v1/account/deposit")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId, "amount": "50"}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
        and: "Check balance"
        mockMvc.perform(post("/api/v1/account/info")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath('$.balance', is(50)))
        and: "Withdrawal on 55"
        mockMvc.perform(post("/api/v1/account/withdraw")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId, "amount": "55"}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath('$.resultCode', is("NotEnoughMoney")))
        and: "Check balance"
        mockMvc.perform(post("/api/v1/account/info")
                .contentType(APPLICATION_JSON_VALUE)
                .content("""{"id": $accountId}""".toString())
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath('$.balance', is(50)))
    }

}
