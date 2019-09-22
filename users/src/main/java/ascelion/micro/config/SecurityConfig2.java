//package ascelion.micro.config;
//
//import java.io.IOException;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.sql.DataSource;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
///**
// * Configures security to use JDBC and Basic Authentication.
// */
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//	private static final String ROLES_QUERY = "SELECT username, 'ROLE_' || rolename FROM authz_roles WHERE username = ?";
//	private static final String USERS_QUERY = "SELECT username, password, NOT disabled FROM authz_users WHERE username = ?";
//
//	private final DataSource ds;
//	private final PasswordEncoder passwordEncoder;
//
//	@Autowired
//	public void globalUserDetails(final AuthenticationManagerBuilder auth) throws Exception {
//		auth.jdbcAuthentication()
//				.dataSource(this.ds)
//				.passwordEncoder(this.passwordEncoder)
//				.usersByUsernameQuery(USERS_QUERY)
//				.authoritiesByUsernameQuery(ROLES_QUERY);
//	}
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//	//@formatter:off
//		http
//				.sessionManagement()
//				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//			.and()
//				.exceptionHandling()
//					.accessDeniedHandler(this::forbidden)
//					.authenticationEntryPoint(this::unauthorized)
//			.and()
//				.authorizeRequests()
//					.anyRequest()
//					.authenticated()
//			.and()
//				.httpBasic()
//			.and()
//				.csrf().disable()
//				;
//	//@formatter:on
//	}
//
//	@Override
//	@Bean
//	public AuthenticationManager authenticationManagerBean() throws Exception {
//		return super.authenticationManagerBean();
//	}
//
//	@Override
//	@Bean
//	public UserDetailsService userDetailsServiceBean() throws Exception {
//		return super.userDetailsServiceBean();
//	}
//
//	private void unauthorized(HttpServletRequest req, HttpServletResponse rsp, AuthenticationException ex) throws IOException, ServletException {
//		rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
//	}
//
//	private void forbidden(HttpServletRequest req, HttpServletResponse rsp, AccessDeniedException ex) throws IOException, ServletException {
//		rsp.sendError(HttpServletResponse.SC_FORBIDDEN);
//	}
//}
