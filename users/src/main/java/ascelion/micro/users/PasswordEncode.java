package ascelion.micro.users;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordEncode {
	private final PasswordEncoder passwordEncoder;

	@PrePersist
	@PreUpdate
	public void encode(User u) {
		u.encode(this.passwordEncoder);
	}
}
