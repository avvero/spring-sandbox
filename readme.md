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