package ascelion.micro.tests;

import java.util.Set;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

@Component
public class MockOauthContextFactory implements WithSecurityContextFactory<WithRole> {

	@Override
	public SecurityContext createSecurityContext(WithRole annotation) {
		final SecurityContext context = SecurityContextHolder.createEmptyContext();
		final Set<GrantedAuthority> authorities = stream(annotation.value())
				.map(a -> "ROLE_" + a)
				.map(SimpleGrantedAuthority::new)
				.collect(toSet());

		final OAuth2Request request = new OAuth2Request(null, null, authorities, true, null, null, null, null, null);
		final Authentication auth = new OAuth2Authentication(request, null);

		context.setAuthentication(auth);

		return context;
	}

}
