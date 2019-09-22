package ascelion.micro.users;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ascelion.micro.endpoint.EntityEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UsersController extends EntityEndpoint<User, UserRequest> {

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UsersController(UsersRepository repo) {
		super(repo);
	}

	@Override
	public User updateEntity(@NotNull UUID id, @NotNull @Valid UserRequest request) {
		request.setPassword(this.passwordEncoder.encode(request.getPassword()));

		return super.updateEntity(id, request);
	}

	@Override
	public User patchEntity(@NotNull UUID id, @NotNull @Valid UserRequest request) {
		request.setPassword(this.passwordEncoder.encode(request.getPassword()));

		return super.patchEntity(id, request);
	}
}
