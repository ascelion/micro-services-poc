package ascelion.micro.tests;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RUNTIME)
@WithSecurityContext(factory = MockOauthContextFactory.class)
public @interface WithRole {
	String[] value();
}
