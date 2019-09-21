package ascelion.micro.spring;

import java.util.Arrays;

import ascelion.flyway.csv.CSVMigrationResolver;

import static java.util.Arrays.asList;

import org.flywaydb.core.api.resolver.MigrationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = {
		UserDetailsServiceAutoConfiguration.class,
})
@EnableSwagger2
@Import({
		BeanValidatorPluginsConfiguration.class,
})
public class ProductsApplication {
	static private final Logger LOG = LoggerFactory.getLogger(ProductsApplication.class);
	static private final String PACKAGE_NAME = ProductsApplication.class.getPackage().getName();

	public static void main(String[] args) {
		SpringApplication.run(ProductsApplication.class, args);
	}

	static public final String IS_USER = "hasRole('ROLE_USER')";
	static public final String IS_ADMIN = "hasRole('ROLE_ADMIN')";

	/**
	 * Prefer to hard code the base URI.
	 */
	@Bean
	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
		return factory -> factory.setContextPath("/api");
	}

	@Bean
	public FlywayConfigurationCustomizer flywayConfigurationCustomizer() {
		return configuration -> {
			MigrationResolver[] resolvers = configuration.getResolvers();
			final int size = resolvers.length;

			resolvers = Arrays.copyOf(resolvers, size + 1);
			resolvers[size] = new CSVMigrationResolver();

			LOG.info("Flyway resolvers: {}", asList(resolvers));
			LOG.info("Flyway locations: {}", asList(configuration.getLocations()));

			configuration.resolvers(resolvers);
		};
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage(PACKAGE_NAME))
				.paths(PathSelectors.any())
				.build();
	}
}
