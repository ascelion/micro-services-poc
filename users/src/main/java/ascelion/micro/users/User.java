package ascelion.micro.users;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ascelion.micro.model.AbstractEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "authz_users")
@Getter
@Setter
public class User extends AbstractEntity {
	@NotNull
	@Size(min = 6)
	private String username;

	@NotNull
	@Size(min = 6)
	@JsonIgnore
	private String password;
}
