package ascelion.micro.users;

import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.EntityEndpoint;

@Endpoint("users")
public class UsersController extends EntityEndpoint<User, UserRequest> {
	public UsersController(UsersRepository repo) {
		super(repo);
	}
}
