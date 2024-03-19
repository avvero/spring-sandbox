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

## Request captor for MockRestServiceServer

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

## Request captor for Wiremock

The `WiredRequestCaptor` class is designed to capture HTTP requests in tests. This class keeps track of how many times it 
has matched a request (times), the body of the last request both as a string (bodyString) and as a parsed 
JSON object (body), and the headers of the last request (headers).

Please refer to the test below to get an insight.
```groovy
def "Forecast for provided city London is 42"() {
    setup:
    StubMapping forecastMapping = wireMockServer.stubFor(WireMock.post(WireMock.urlEqualTo("/forecast"))
            .willReturn(WireMock.aResponse()
                    .withBody('{"result": "42"}')
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")))
    def requestCaptor = new WiredRequestCaptor(wireMockServer, forecastMapping)
    when:
    def forecast = weatherService.getForecast("London")
    then:
    forecast == "42"
    requestCaptor.times == 1
    requestCaptor.body.city == "London"
    requestCaptor.headers.get("Content-Type") == ["application/json"]
}
```

Include the necessary dependency in your project's build configuration to utilize Request Captor:
```groovy
testImplementation 'pw.avvero:request-captor-wired:1.0.0'
```

## Performance Improvement via PostgreSQL Container Configuration

### Overview

In our continuous effort to optimize our test suite, a significant resource-intensive method was identified during
profiling. This method is related to the PostgreSQL JDBC driver's password protection mechanism during authentication.
By modifying our PostgreSQL container's environment settings, we can achieve a noteworthy performance improvement.

#### Identified Resource-Intensive Method

The profiling session pinpointed a method consuming considerable resources:

- `java.lang.invoke.VarHandleByteArrayAsInts$ArrayHandle.index`
- `org.postgresql.shaded.com.ongres.scram.common.util.CryptoUtil.hi`
- `org.postgresql.shaded.com.ongres.scram.common.ScramMechanisms.saltedPassword`

These methods are part of the PostgreSQL JDBC driver, specifically involved in password protection during the authentication process.

### Solution: Disabling Password Protection

To bypass this resource-intensive password protection, we can disable it by setting an environment variable in our 
PostgreSQL container definition. This change instructs the container to use the "trust" authentication method, 
eliminating the need for password verification.

#### Code Adjustment

Modify the PostgreSQL container configuration as follows:

```java
private final static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14"))
        .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust") // Disable password protection
        .waitingFor(Wait.forListeningPort());
```

### Performance Benchmark Results

A [synthetic benchmark](benchmark/src/jmh/java/pw/avvero/spring/sandbox/PostgresqlContainerAuthModeBenchmark.java) 
demonstrated a performance improvement when using the "trust" authentication method:
```md
Benchmark                                                               Mode  Cnt  Score   Error  Units
PostgresqlContainerAuthModeBenchmark.methodDefaultOneConnection           ss    4  1,056 ± 0,050   s/op
PostgresqlContainerAuthModeBenchmark.methodTrustOneConnection             ss   20  1,141 ± 0,082   s/op
PostgresqlContainerAuthModeBenchmark.methodDefaultOneHundredConnection    ss   14  2,201 ± 0,095   s/op
PostgresqlContainerAuthModeBenchmark.methodTrustOneHundredConnection      ss   20  1,635 ± 0,069   s/op
```