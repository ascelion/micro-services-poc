package ascelion.micro;

import javax.servlet.http.HttpServletRequest;

import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class UserClientConfig {

	@Autowired
	private HttpServletRequest request;

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}

	@Bean
	RequestInterceptor feignAuthzHeader() {
		return template -> {
			template.header(HttpHeaders.AUTHORIZATION, this.request.getHeader(HttpHeaders.AUTHORIZATION));
		};
	}
}