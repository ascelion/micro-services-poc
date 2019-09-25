package ascelion.micro.shared.config;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

@EnableResourceServer
@Configuration
@RequiredArgsConstructor
public class SharedResourceServerConfig extends ResourceServerConfigurerAdapter {

	static class DetailsAccessTokenConverter extends DefaultAccessTokenConverter {

		@Override
		public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
			final OAuth2Authentication authentication = super.extractAuthentication(claims);

			authentication.setDetails(claims);

			return authentication;
		}

	}

	private final DefaultTokenServices tokenServices;

	@Override
	public void configure(final HttpSecurity http) throws Exception {
		//@formatter:off
		http
			.exceptionHandling()
				.authenticationEntryPoint(this::unauthorized)
				.accessDeniedHandler(this::forbidden)
			.and()
				.authorizeRequests()
				.antMatchers("/error").permitAll()
				.antMatchers(HttpMethod.GET, "/**").hasRole("USERS")
				.antMatchers(HttpMethod.HEAD, "/**").hasRole("USERS")
				.antMatchers(HttpMethod.OPTIONS, "/**").hasRole("USERS")
				.antMatchers(HttpMethod.DELETE, "/**").hasRole("ADMINS")
				.antMatchers(HttpMethod.PATCH, "/**").hasRole("ADMINS")
				.antMatchers(HttpMethod.POST, "/**").hasRole("ADMINS")
				.antMatchers(HttpMethod.PUT, "/**").hasRole("ADMINS")
				.anyRequest().authenticated()
			.and()
				.csrf().disable()
			;
		//@formatter:on
	}

	@Override
	public void configure(final ResourceServerSecurityConfigurer resources) {
		resources.tokenServices(this.tokenServices);
	}

	private void unauthorized(HttpServletRequest req, HttpServletResponse rsp, AuthenticationException ex) throws IOException, ServletException {
		rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	private void forbidden(HttpServletRequest req, HttpServletResponse rsp, AccessDeniedException ex) throws IOException, ServletException {
		rsp.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

}
