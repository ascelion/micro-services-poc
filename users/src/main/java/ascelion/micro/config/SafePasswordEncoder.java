package ascelion.micro.config;

import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SafePasswordEncoder implements PasswordEncoder {

	static public final Pattern PATTERN = Pattern.compile("^\\$2[aby]?\\$\\d{1,2}\\$[.\\/A-Za-z0-9]{53}$");

	private final PasswordEncoder delegate;

	public SafePasswordEncoder(SecurityProperties config) {
		this.delegate = new BCryptPasswordEncoder(config.getStrength());
	}

	@Override
	public String encode(CharSequence rawPassword) {
		return PATTERN.matcher(rawPassword).matches()
				? rawPassword.toString()
				: this.delegate.encode(rawPassword);
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return this.delegate.matches(rawPassword, encodedPassword);
	}

}
