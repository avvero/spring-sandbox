package pw.avvero.spring.sandbox

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

import static org.testcontainers.containers.BindMode.READ_WRITE

class GatlingTests extends Specification {

    @Unroll
    def "Launch gatling simulation"() {
        setup:
        def workingDirectory = new File("").getAbsolutePath()
        def reportDirectory = workingDirectory + "/build/" + new Date().getTime()
        for (String tempDir : ["/logs", "/jfr", "/gatling-results"]) {
            File tempDirectory = new File(tempDir)
            if (!tempDirectory.exists() && !tempDirectory.mkdirs()) {
                throw new UnsupportedOperationException()
            }
            tempDirectory.setReadable(true, false)
            tempDirectory.setWritable(true, false)
            tempDirectory.setExecutable(true, false)
        }
        def network = Network.newNetwork();
        def helper = new GenericContainer<>("alpine:3.17")
                .withNetwork(network)
                .withCommand("top")
        helper.start()
        when:
        def wiremock = new GenericContainer<>("wiremock/wiremock:3.5.4")
                .withNetwork(network)
                .withNetworkAliases("wiremock")
                .withFileSystemBind(workingDirectory + "/src/test/resources/wiremock/mappings", "/home/wiremock/mappings", READ_WRITE)
                .withCommand("--no-request-journal")
                .withLogConsumer(new FileHeadLogConsumer(reportDirectory + "/logs/" + "wiremock" + ".log"))
                .waitingFor(new LogMessageWaitStrategy().withRegEx(".*https://wiremock.io/cloud.*"))
        wiremock.start()
        then:
        "Ok" == helper.execInContainer("wget", "-O", "-", "http://wiremock:8080/health").getStdout()
        when:
        def javaOpts = ' -Xloggc:/tmp/gc/gc.log -XX:+PrintGCDetails' + ' -XX:+UnlockDiagnosticVMOptions' + ' -XX:+FlightRecorder' + ' -XX:StartFlightRecording:settings=default,dumponexit=true,disk=true,duration=120s,filename=/tmp/jfr/flight.jfr'
        def sandbox = new GenericContainer<>(image)
                .withNetwork(network)
                .withNetworkAliases("sandbox")
                .withFileSystemBind(reportDirectory + "/logs", "/tmp/gc", READ_WRITE)
                .withFileSystemBind(reportDirectory + "/jfr", "/tmp/jfr", READ_WRITE)
                .withEnv([
                        'JAVA_OPTS'      : javaOpts,
                        'app.weather.url': "http://wiremock:8080"
                ])
                .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Started Application.*"))
                .withStartupTimeout(Duration.ofSeconds(60L * 5))
        sandbox.start()
        then:
        "" != helper.execInContainer("wget", "-O", "-", "http://sandbox:8080/actuator/health").getStdout()
        when:
        def gatling = new GenericContainer<>("denvazh/gatling:3.2.1")
                .withFileSystemBind(reportDirectory + "/gatling-results", "/opt/gatling/results", READ_WRITE)
                .withFileSystemBind(workingDirectory + "/src/gatling/scala", "/opt/gatling/user-files/simulations", READ_WRITE)
                .withFileSystemBind(workingDirectory + "/src/gatling/resources", "/opt/gatling/conf", READ_WRITE)
                .withEnv("sandbox", "http://sandbox:8080")
                .withCommand("-s", "MainSimulation")
                .waitingFor(new LogMessageWaitStrategy()
                        .withRegEx(".*Please open the following file: /opt/gatling/results.*")
                        .withStartupTimeout(Duration.ofSeconds(60L * 5))
                );
        gatling.start()
        then:
        noExceptionThrown()
        cleanup:
        helper.stop()
        wiremock.stop()
        sandbox.stop()
        gatling.stop()
        where:
        image                  | _
        'avvero/sandbox:1.0.0' | _
    }

}
