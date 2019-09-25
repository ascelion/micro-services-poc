package ascelion.micro.shared.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class SharedTokenConfig {

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

	@Bean
	public TokenStore tokenStore(JwtAccessTokenConverter accessTokenConverter) {
		return new JwtTokenStore(accessTokenConverter);
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		final JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();

		accessTokenConverter.setAccessTokenConverter(new DetailsAccessTokenConverter());
		accessTokenConverter.setSigningKey(this.signKey);

		return accessTokenConverter;
	}

	@Bean
	public DefaultTokenServices tokenServices(TokenStore tokenStore) {
		final DefaultTokenServices tokenServices = new DefaultTokenServices();

		tokenServices.setTokenStore(tokenStore);
		tokenServices.setSupportRefreshToken(true);

		return tokenServices;
	}

}
