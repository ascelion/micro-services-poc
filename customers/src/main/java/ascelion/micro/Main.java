package ascelion.micro;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory.createHttpServer;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.Configuration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.bridge.SLF4JBridgeHandler;

@ApplicationScoped
public class Main {
	private static final URI BASE_URI = URI.create("http://localhost:8080/api/v1");

	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		final String rootLevel = System.getProperty("jul.root.level", "INFO");

		LogManager.getLogManager().getLogger("").setLevel(Level.parse(rootLevel));
	}

	public static void main(String[] args) throws IOException {
		final ResourceConfig conf = new ResourceConfig()
				.property(ServerProperties.PROVIDER_PACKAGES, Main.class.getPackage().getName());
		final HttpServer server = createHttpServer(BASE_URI, conf, false);

		Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));

		server.start();
	}

	@Produces
	@ApplicationScoped
	Configuration configuration() {
		return Flyway.configure()
				.dataSource("jdbc:postgresql:customers", "customers", "customers")
				.locations("db");
	}

	@Produces
	@ApplicationScoped
	public EntityManagerFactory entityManagerFactory() {
		return Persistence.createEntityManagerFactory("default");
	}

	public void close(@Disposes EntityManagerFactory emf) {
		if (emf.isOpen()) {
			emf.close();
		}
	}

	@Produces
	@RequestScoped
	public EntityManager create(EntityManagerFactory emf) {
		return emf.createEntityManager();
	}

	public void close(@Disposes EntityManager em) {
		if (em.isOpen()) {
			em.close();
		}
	}
}
