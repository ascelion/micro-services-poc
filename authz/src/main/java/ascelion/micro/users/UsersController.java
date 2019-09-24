package ascelion.micro.users;

import ascelion.micro.endpoint.Endpoint;
import ascelion.micro.endpoint.EntityEndpoint;

@Endpoint("users")
public class UsersController extends EntityEndpoint<User, UserRequest> {
	public UsersController(UsersRepository repo) {
		super(repo);
	}
}
