package ascelion.micro.config;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private static final String USERS_QUERY = ""
			+ "SELECT username, password, NOT disabled FROM authz_users"
			+ " WHERE username = ?";
	private static final String ROLES_QUERY = ""
			+ "SELECT u.username, 'ROLE_' || r.rolename FROM authz_users u"
			+ " JOIN authz_users_roles m ON u.id = m.user_id"
			+ " JOIN authz_roles r on m.role_id = r.id"
			+ " WHERE u.username = ?";

	private final DataSource dataSource;
	private final PasswordEncoder passwordEncoder;

	@Autowired
	public void globalUserDetails(final AuthenticationManagerBuilder auth) throws Exception {
		auth
				.jdbcAuthentication()
				.dataSource(this.dataSource)
				.passwordEncoder(this.passwordEncoder)
				.usersByUsernameQuery(USERS_QUERY)
				.authoritiesByUsernameQuery(ROLES_QUERY);
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		//@formatter:off
		http
			.authorizeRequests()
				.anyRequest().authenticated()
			.and()
				.httpBasic()
			.and()
				.csrf().disable()
				;
		//@formatter:on
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
