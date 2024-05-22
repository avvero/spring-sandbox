package pw.avvero.spring.sandbox

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration

import static org.testcontainers.containers.BindMode.READ_WRITE

class YandexTankTests extends Specification {

    @Unroll
    def "Launch yandex-tank simulation"() {
        setup:
        def workingDirectory = new File("").getAbsolutePath()
        def reportDirectory = workingDirectory + "/build/" + new Date().getTime()
        for (String tempDir : ["/logs", "/jfr", "/gatling-results"]) {
            File tempDirectory = new File(reportDirectory + tempDir)
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
        def postgres = new GenericContainer<>("postgres:10")
                .withNetwork(network)
                .withNetworkAliases("postgres")
                .withEnv(["POSTGRES_USER": "sandbox", "POSTGRES_DB": "sandbox", "POSTGRES_PASSWORD": "sandbox"])
        postgres.start()
        def javaOpts = ' -Xloggc:/tmp/gc/gc.log -XX:+PrintGCDetails' + ' -XX:+UnlockDiagnosticVMOptions' + ' -XX:+FlightRecorder' + ' -XX:StartFlightRecording:settings=default,dumponexit=true,disk=true,duration=120s,filename=/tmp/jfr/flight.jfr'
        def sandbox = new GenericContainer<>(image)
                .withNetwork(network)
                .withNetworkAliases("sandbox")
                .withFileSystemBind(reportDirectory + "/logs", "/tmp/gc", READ_WRITE)
                .withFileSystemBind(reportDirectory + "/jfr", "/tmp/jfr", READ_WRITE)
                .withEnv([
                        'JAVA_OPTS'                                     : javaOpts,
                        'app.weather.url'                               : 'http://wiremock:8080',
                        'spring.datasource.url'                         : 'jdbc:postgresql://postgres:5432/sandbox',
                        'spring.datasource.username'                    : 'sandbox',
                        'spring.datasource.password'                    : 'sandbox',
                        'spring.jpa.properties.hibernate.default_schema': 'sandbox'
                ])
                .withLogConsumer(new FileHeadLogConsumer(reportDirectory + "/logs/" + "sandbox" + ".log"))
                .waitingFor(new LogMessageWaitStrategy().withRegEx(".*Started SandboxApplication.*"))
                .withStartupTimeout(Duration.ofSeconds(10))
        sandbox.start()
        then:
        "" != helper.execInContainer("wget", "-O", "-", "http://sandbox:8080/actuator/health").getStdout()
        when:
        def tank = new GenericContainer<>("yandex/yandex-tank")
                .withNetwork(network)
                .withFileSystemBind(workingDirectory + "/src/test/resources/yandex-tank", "/var/loadtest", READ_WRITE)
                .withEnv("SERVICE_URL", "http://sandbox:8080")
//                .withCreateContainerCmdModifier(cmd -> {
//                    cmd.getHostConfig()
//                            .withMemory(1024 * 1024 * 1024L)
//                            .withMemorySwap(1024 * 1024 * 1024L);
//                })
                .withLogConsumer(new FileHeadLogConsumer(reportDirectory + "/logs/" + "yandex-tank" + ".log"))
                .waitingFor(new LogMessageWaitStrategy()
                        .withRegEx(".*Please open the following file: /opt/gatling/results.*")
                        .withStartupTimeout(Duration.ofSeconds(60L * 5))
                );
        tank.start()
        then:
        noExceptionThrown()
        cleanup:
        helper?.stop()
        wiremock?.stop()
        sandbox?.stop()
        postgres?.stop()
        tank?.stop()
        where:
        image                  | _
        'avvero/sandbox:1.0.0' | _
    }

}
