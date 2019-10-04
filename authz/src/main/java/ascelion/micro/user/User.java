package ascelion.micro.user;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ascelion.micro.shared.model.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "authz_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(PasswordEncode.class)
public class User extends AbstractEntity<User> {
	@NotNull
	@Size(min = 6)
	private String username;

	@NotNull
	@Size(min = 6)
	@JsonIgnore
	private String password;

	public void setPassword(String password) {
		this.password = password;
	}

	void encode(PasswordEncoder pe) {
		this.password = pe.encode(this.password);
	}
}
