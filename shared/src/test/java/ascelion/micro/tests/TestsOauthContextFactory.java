package ascelion.micro.tests;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

@Component
public class TestsOauthContextFactory implements WithSecurityContextFactory<WithRole> {

	@Override
	public SecurityContext createSecurityContext(WithRole annotation) {
		final var context = SecurityContextHolder.createEmptyContext();
		final var authorities = stream(annotation.value())
				.map(a -> "ROLE_" + a)
				.map(SimpleGrantedAuthority::new)
				.collect(toSet());

		final var request = new OAuth2Request(null, null, authorities, true, null, null, null, null, null);
		final var auth = new OAuth2Authentication(request, null);

		context.setAuthentication(auth);

		return context;
	}

}
