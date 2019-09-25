package ascelion.micro.tests;

import ascelion.micro.shared.config.SharedResourceServerConfig;
import ascelion.micro.shared.config.SharedTokenConfig;
import ascelion.micro.shared.endpoint.EndpointHandlerMapping;
import ascelion.micro.shared.endpoint.ExceptionHandlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@Import({
		EndpointHandlerMapping.class,
		ExceptionHandlers.class,
		SharedResourceServerConfig.class,
		SharedTokenConfig.class,
})
@RequiredArgsConstructor
public class TestsResourceServerConfig extends ResourceServerConfigurerAdapter {
//	private final SharedResourceServerConfig shared;

	@Override
	public void configure(ResourceServerSecurityConfigurer security) throws Exception {
//		this.shared.configure(security);
//
		security.stateless(false);
	}

//	@Override
//	public void configure(HttpSecurity http) throws Exception {
//		this.shared.configure(http);
//	}
}