package ascelion.micro.tests;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import ascelion.micro.shared.config.SharedFlywayConfig;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

@Retention(RUNTIME)
@Target(TYPE)
@ActiveProfiles({ "itest", "dev" })
@SpringBootTest(classes = {
		SharedFlywayConfig.class,
})
@EnableJpaRepositories("ascelion.micro")
@EntityScan("ascelion.micro")
@EnableAutoConfiguration(exclude = {
		UserDetailsServiceAutoConfiguration.class,
})
public @interface JpaEntityIT {

}
