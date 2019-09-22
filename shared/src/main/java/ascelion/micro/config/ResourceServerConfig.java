package ascelion.micro.config;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@EnableResourceServer
@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	static class DetailsAccessTokenConverter extends DefaultAccessTokenConverter {

		@Override
		public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
			final OAuth2Authentication authentication = super.extractAuthentication(claims);

			authentication.setDetails(claims);

			return authentication;
		}

	}

	@Value("${security.signKey:none}")
	private String signKey;

	private DefaultTokenServices tokenServices;
	private JwtAccessTokenConverter accessTokenConverter;
	private JwtTokenStore tokenStore;

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
				.antMatchers("/**").hasRole("ADMINS")
				.anyRequest().authenticated()
			.and()
				.csrf().disable()
			;
		//@formatter:on
	}

	@Override
	public void configure(final ResourceServerSecurityConfigurer resources) {
		resources.tokenServices(tokenServices());
	}

	@Bean
	public TokenStore tokenStore() {
		if (this.tokenStore == null) {
			this.tokenStore = new JwtTokenStore(accessTokenConverter());
		}

		return this.tokenStore;
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		if (this.accessTokenConverter == null) {
			this.accessTokenConverter = new JwtAccessTokenConverter();

			this.accessTokenConverter.setAccessTokenConverter(new DetailsAccessTokenConverter());
			this.accessTokenConverter.setSigningKey(this.signKey);
		}

		return this.accessTokenConverter;
	}

	@Bean
	public DefaultTokenServices tokenServices() {
		if (this.tokenServices == null) {
			this.tokenServices = new DefaultTokenServices();

			this.tokenServices.setTokenStore(tokenStore());
			this.tokenServices.setSupportRefreshToken(true);
		}

		return this.tokenServices;
	}

	private void unauthorized(HttpServletRequest req, HttpServletResponse rsp, AuthenticationException ex) throws IOException, ServletException {
		rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	private void forbidden(HttpServletRequest req, HttpServletResponse rsp, AccessDeniedException ex) throws IOException, ServletException {
		rsp.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

}
