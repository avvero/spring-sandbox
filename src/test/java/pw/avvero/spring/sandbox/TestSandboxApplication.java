package pw.avvero.spring.sandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestSandboxApplication {

	public static void main(String[] args) {
		SpringApplication.from(SandboxApplication::main).with(TestSandboxApplication.class).run(args);
	}

}
