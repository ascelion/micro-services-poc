package ascelion.micro.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@RequiredArgsConstructor
public class TokenConfig {
	private final SecurityProperties config;

	@Bean
	@Primary
	public JwtAccessTokenConverter primaryAccessTokenConverter() {
		final JwtAccessTokenConverter cvt = new JwtAccessTokenConverter();

		cvt.setSigningKey(this.config.getSignKey());

		return cvt;
	}
}
