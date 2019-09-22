package ascelion.micro.tests;

import ascelion.micro.config.ResourceServerConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@Import(ResourceServerConfig.class)
@RequiredArgsConstructor
public class ResourceServerTestConfiguration extends ResourceServerConfigurerAdapter {
	private final ResourceServerConfig configuration;

	@Override
	public void configure(ResourceServerSecurityConfigurer security) throws Exception {
		this.configuration.configure(security);

		security.stateless(false);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		this.configuration.configure(http);
	}
}