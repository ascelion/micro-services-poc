package ascelion.micro.config;

import java.io.IOException;

import ascelion.micro.shared.config.JwtProperties;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableConfigurationProperties(OauthProperties.class)
@RequiredArgsConstructor
public class TokenConfig {
	private final JwtProperties jwt;

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() throws IOException {
		final var cvt = new JwtAccessTokenConverter();

		this.jwt.configure(cvt);

		return cvt;
	}
}
