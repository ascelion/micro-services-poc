package ascelion.micro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication(exclude = {
		UserDetailsServiceAutoConfiguration.class,
})
@EnableEurekaServer
public class Main {
	static {
		System.setProperty("server.servlet.context-path", "/cloud");
	}

	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}
}
