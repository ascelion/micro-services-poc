package ascelion.micro.shared.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.FileCopyUtils;

@ConfigurationProperties(prefix = "oauth.jwt")
@Getter
@Setter
@RequiredArgsConstructor
public class JwtProperties {
	private final ResourceLoader rld;

	private String signKey;
	private String verifyKey;

	public void configure(JwtAccessTokenConverter cvt) throws IOException {
		if (isNotBlank(this.verifyKey)) {
			final Resource res = this.rld.getResource(this.verifyKey);

			if (res.isReadable()) {
				try (Reader rd = new InputStreamReader(res.getInputStream(), "ISO-8859-1")) {
					cvt.setVerifier(new RsaVerifier(FileCopyUtils.copyToString(rd)));
				}
			} else {
				cvt.setSigningKey(this.verifyKey);
			}
		}
		if (isNotBlank(this.signKey)) {
			final Resource res = this.rld.getResource(this.signKey);

			if (res.isReadable()) {
				try (Reader rd = new InputStreamReader(res.getInputStream(), "ISO-8859-1")) {
					cvt.setSigner(new RsaSigner(FileCopyUtils.copyToString(rd)));
				}
			} else {
				cvt.setSigningKey(this.signKey);
			}
		}
	}
}
