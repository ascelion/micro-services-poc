package ascelion.micro.shared.config;

import java.io.IOException;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class SharedTokenConfig {

	static class DetailsAccessTokenConverter extends DefaultAccessTokenConverter {

		@Override
		public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
			final var authentication = super.extractAuthentication(claims);

			authentication.setDetails(claims);

			return authentication;
		}

	}

	private final JwtProperties jwt;

	@Bean
	public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
		return new JwtTokenStore(accessTokenConverter);
	}

	@Bean
	public DefaultTokenServices tokenServices(TokenStore tokenStore) {
		final var tokenServices = new DefaultTokenServices();

		tokenServices.setTokenStore(tokenStore);
		tokenServices.setSupportRefreshToken(true);

		return tokenServices;
	}

	@Bean
	@ConditionalOnMissingBean
	public JwtAccessTokenConverter accessTokenConverter() throws IOException {
		final var cvt = new JwtAccessTokenConverter();

		this.jwt.configure(cvt);
		cvt.setAccessTokenConverter(new DetailsAccessTokenConverter());

		return cvt;
	}

}
