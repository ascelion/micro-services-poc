package ascelion.micro.roles;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import ascelion.micro.shared.model.AbstractEntity;
import ascelion.micro.users.PasswordEncode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "authz_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(PasswordEncode.class)
public class Role extends AbstractEntity<Role> {
	private String rolename;
}
