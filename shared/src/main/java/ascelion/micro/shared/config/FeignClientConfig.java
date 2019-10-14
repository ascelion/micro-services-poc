package ascelion.micro.shared.config;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import feign.Client;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@ConditionalOnClass(Client.class)
public class FeignClientConfig {

	static private final ThreadLocal<String> AUTHZ = new ThreadLocal<>();

	/**
	 * Feign client called from threads not bound to a request.
	 */
	static public void setAuthorization(String authz) {
		if (authz != null) {
			AUTHZ.set(authz);
		} else {
			AUTHZ.remove();
		}
	}

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	@Bean
	RequestInterceptor feignAuthzHeader() {
		return template -> {
			final String authz = AUTHZ.get();

			if (authz != null) {
				template.header(AUTHORIZATION, authz);
			}
		};
	}

	@Bean
	public FilterRegistrationBean<Filter> feignAuthorization() {
		final var frb = new FilterRegistrationBean<>();

		frb.setFilter(authzHeaderFilter());
		frb.addUrlPatterns("/*");
		frb.setOrder(Ordered.LOWEST_PRECEDENCE);

		return frb;
	}

	private Filter authzHeaderFilter() {
		return (request, response, chain) -> {
			setAuthorization(((HttpServletRequest) request).getHeader(AUTHORIZATION));

			try {
				chain.doFilter(request, response);
			} finally {
				setAuthorization(null);
			}
		};
	}

}