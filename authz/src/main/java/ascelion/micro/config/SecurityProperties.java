package ascelion.micro.config;

import java.util.Map;

import static java.util.Collections.emptyMap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security")
@Getter
@Setter
public class SecurityProperties {
	@Getter
	@Setter
	static public class Details {
		private String secret;
		private String[] authorities = new String[0];
		private String[] grantTypes = new String[0];
		private String[] resourceIds = new String[0];
		private String[] scopes = new String[0];
		private boolean autoApprove = true;
		private String[] autoApproveScopes = new String[0];
		private String[] redirectUris = new String[0];
		private int accessTokenValidity = 3600;
		private int refreshTokenValidity = 86400;
		private Map<String, ?> info = emptyMap();

	}

	private String signKey = "none";
	private int strength = -1;

	private Map<String, Details> clients = emptyMap();
}
