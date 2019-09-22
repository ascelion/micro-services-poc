package ascelion.micro.config;

import java.util.Arrays;

import ascelion.flyway.csv.CSVMigrationResolver;

import static java.util.Arrays.asList;

import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.resolver.MigrationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig implements FlywayConfigurationCustomizer {
	static private final Logger LOG = LoggerFactory.getLogger(FlywayConfig.class);

	@Override
	public void customize(FluentConfiguration cf) {
		MigrationResolver[] resolvers = cf.getResolvers();
		final int size = resolvers.length;

		resolvers = Arrays.copyOf(resolvers, size + 1);
		resolvers[size] = new CSVMigrationResolver();

		LOG.info("Flyway resolvers: {}", asList(resolvers));
		LOG.info("Flyway locations: {}", asList(cf.getLocations()));

		cf.resolvers(resolvers);
	}

}
