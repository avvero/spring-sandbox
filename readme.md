# Sandbox for spring related stuff

## Testcontainers configuration

Testcontainers [configuration](https://github.com/avvero/spring-sandbox/blob/main/src/test/java/pw/avvero/spring/sandbox/ContainersConfiguration.java)
for `postgres` using `@ServiceConnection` feature from spring.

## Ordering the Chaos: Arranging HTTP Request Testing in Spring

Code related to article, where I would like to describe an approach to writing tests with a clear division into separate 
stages, each performing its specific role. This facilitates the creation of tests that are easier to read, understand, 
and maintain.

The discussion will focus on using the Arrange-Act-Assert methodology for integration testing in the Spring Framework 
with mocking of HTTP requests to external resources encountered during the execution of the tested code within the 
system behavior. The tests under consideration are written using the Spock Framework in the Groovy language. 
MockRestServiceServer will be used as the mocking mechanism. There will also be a few words about WireMock.

[Link to article (RUS)](https://habr.com/ru/articles/781812)

[Link to article (ENG)](https://medium.com/@avvero.abernathy/ordering-chaos-arranging-http-request-testing-in-spring-c625520d2418)

## Request captor

The `RequestCaptor` class is designed to capture HTTP requests in tests. This class keeps track of how many times it 
has matched a request (times), the body of the last request both as a string (bodyString) and as a parsed 
JSON object (body), and the headers of the last request (headers).

Please refer to the test below to get an insight.
```groovy
def "Forecast for provided city London is 42"() {
    setup:          // (1)
    def requestCaptor = new RequestCaptor()
    mockServer.expect(manyTimes(), requestTo("https://external-weather-api.com/forecast")) // (2)
            .andExpect(method(HttpMethod.POST))
            .andExpect(requestCaptor)                                                      // (3)
            .andRespond(withSuccess('{"result": "42"}', MediaType.APPLICATION_JSON));      // (4)
    when:          // (5)
    def forecast = weatherService.getForecast("London")
    then:          // (6)
    forecast == "42"
    requestCaptor.times == 1            // (7)
    requestCaptor.body.city == "London" // (8)
    requestCaptor.headers.get("Content-Type") == ["application/json"]
}
```

Include the necessary dependency in your project's build configuration to utilize Request Captor:
```groovy
testImplementation 'pw.avvero:request-captor:1.0.0'
```