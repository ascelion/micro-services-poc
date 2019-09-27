package ascelion.micro.tests;

import ascelion.micro.shared.config.SharedResourceServerConfig;
import ascelion.micro.shared.config.SharedTokenConfig;
import ascelion.micro.shared.endpoint.EndpointHandlerMapping;
import ascelion.micro.shared.endpoint.ExceptionHandlers;
import ascelion.micro.shared.utils.BeanToBeanMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@TestConfiguration
@Import({
		BeanToBeanMapper.class,
		EndpointHandlerMapping.class,
		ExceptionHandlers.class,
		SharedResourceServerConfig.class,
		SharedTokenConfig.class,
		TestsOauthContextFactory.class,
})
@RequiredArgsConstructor
public class TestsResourceServerConfig extends ResourceServerConfigurerAdapter {
	@Override
	public void configure(ResourceServerSecurityConfigurer security) throws Exception {
		security.stateless(false);
	}
}