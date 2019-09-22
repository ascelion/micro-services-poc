package ascelion.micro.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class TokenConfig {
	private final SecurityProperties config;

	@Bean
	public TokenStore tokenStore(JwtAccessTokenConverter cvt) {
		return new JwtTokenStore(cvt);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(this.config.getStrength());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		final JwtAccessTokenConverter cvt = new JwtAccessTokenConverter();

		cvt.setSigningKey(this.config.getSignKey());

		return cvt;
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenServices(TokenStore store) {
		final DefaultTokenServices dts = new DefaultTokenServices();

		dts.setTokenStore(store);
		dts.setSupportRefreshToken(true);

		return dts;
	}
}
