package ascelion.micro.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configures security to use JDBC and Basic Authentication.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class ProductsSecurity extends WebSecurityConfigurerAdapter {
	private static final String ROLES_QUERY = "SELECT username, 'ROLE_' || rolename FROM products_roles WHERE username = ?";
	private static final String USERS_QUERY = "SELECT username, password, NOT disabled FROM products_users WHERE username = ?";

	private final BCryptPasswordEncoder passEnc = new BCryptPasswordEncoder();

	private final DataSource ds;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		        .sessionManagement()
		        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		    .and()
		        .exceptionHandling()
		        .accessDeniedHandler(this::forbidden)
		        .authenticationEntryPoint(this::unauthorized)
		    .and()
		        .authorizeRequests()
		        .anyRequest().authenticated()
		    .and()
		        .httpBasic()
		    .and()
		    	.csrf().disable()
		    	;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
		        .dataSource(this.ds)
		        .passwordEncoder(this.passEnc)
		        .usersByUsernameQuery(USERS_QUERY)
		        .authoritiesByUsernameQuery(ROLES_QUERY);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return this.passEnc;
	}

	private void unauthorized(HttpServletRequest req, HttpServletResponse rsp, AuthenticationException ex) throws IOException, ServletException {
		rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	private void forbidden(HttpServletRequest req, HttpServletResponse rsp, AccessDeniedException ex) throws IOException, ServletException {
		rsp.sendError(HttpServletResponse.SC_FORBIDDEN);
	}
}
