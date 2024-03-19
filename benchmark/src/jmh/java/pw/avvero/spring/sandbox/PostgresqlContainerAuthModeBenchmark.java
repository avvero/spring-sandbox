package pw.avvero.spring.sandbox;

import org.openjdk.jmh.annotations.Benchmark;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgresqlContainerAuthModeBenchmark {

    PostgreSQLContainer<?> container() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:14"))
                .withCommand("postgres -c max_connections=300");
    }

    @Benchmark
    public void methodDefaultOneConnection() throws SQLException {
        PostgreSQLContainer<?> container = container()
                .waitingFor(Wait.forListeningPort());
        container.start();
        //
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUser(container.getUsername());
        dataSource.setPassword(container.getPassword());
        dataSource.setUrl(container.getJdbcUrl());
        execute(dataSource, 1);
        //
        container.stop();
    }

    @Benchmark
    public void methodTrustOneConnection() throws SQLException {
        PostgreSQLContainer<?> container = container()
                .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust") //!!
                .waitingFor(Wait.forListeningPort());
        container.start();
        //
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUser(container.getUsername());
        dataSource.setUrl(container.getJdbcUrl());
        execute(dataSource, 1);
        //
        container.stop();
    }

    @Benchmark
    public void methodDefaultOneHundredConnection() throws SQLException {
        PostgreSQLContainer<?> container = container()
                .waitingFor(Wait.forListeningPort());
        container.start();
        //
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUser(container.getUsername());
        dataSource.setPassword(container.getPassword());
        dataSource.setUrl(container.getJdbcUrl());
        execute(dataSource, 100);
        //
        container.stop();
    }

    @Benchmark
    public void methodTrustOneHundredConnection() throws SQLException {
        PostgreSQLContainer<?> container = container()
                .withEnv("POSTGRES_HOST_AUTH_METHOD", "trust") //!!
                .waitingFor(Wait.forListeningPort());
        container.start();
        //
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUser(container.getUsername());
        dataSource.setUrl(container.getJdbcUrl());
        execute(dataSource, 100);
        //
        container.stop();
    }

    private void execute(PGSimpleDataSource dataSource, int numOfConnections) throws SQLException {
        for (int i = 0; i < numOfConnections; i++) {
            try (Connection connection = dataSource.getConnection()) {
                connection.prepareStatement("select 1;").execute();
            }
        }
    }

}
