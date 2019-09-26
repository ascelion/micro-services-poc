package ascelion.micro.shared;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {
	static public final String ROLE_ROOT = "ROOT";
	static public final String ROLE_ADMIN = "ADMIN";
	static public final String ROLE_USER = "USER";
}
