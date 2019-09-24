package ascelion.micro.users;

import ascelion.micro.endpoint.EntityEndpoint;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UsersController extends EntityEndpoint<User, UserRequest> {
	public UsersController(UsersRepository repo) {
		super(repo);
	}
}
