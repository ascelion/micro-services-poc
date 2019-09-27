package ascelion.micro.tests;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ascelion.micro.shared.SecurityConstants;

@Retention(RetentionPolicy.RUNTIME)
@WithRole(SecurityConstants.ROLE_ROOT)
public @interface WithRoleRoot {
}
