package ascelion.micro.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static ascelion.micro.shared.SecurityConstants.ROLE_ROOT;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@EnableResourceServer
@Configuration
@RequiredArgsConstructor
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	@Override
	public void configure(HttpSecurity http) throws Exception {
	//@formatter:off
		http
			.exceptionHandling()
				.accessDeniedHandler(this::forbidden)
				.authenticationEntryPoint(this::unauthorized)
			.and()
				.authorizeRequests()
				.anyRequest().hasRole(ROLE_ROOT)
			.and()
				.csrf().disable()
				.sessionManagement().disable()
				;
	//@formatter:on
	}

	private void unauthorized(HttpServletRequest req, HttpServletResponse rsp, AuthenticationException ex) throws IOException, ServletException {
		rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	private void forbidden(HttpServletRequest req, HttpServletResponse rsp, AccessDeniedException ex) throws IOException, ServletException {
		rsp.sendError(HttpServletResponse.SC_FORBIDDEN);
	}
}
