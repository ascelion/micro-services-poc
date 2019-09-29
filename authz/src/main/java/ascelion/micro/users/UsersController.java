package ascelion.micro.users;

import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.EntityEndpointBase;

@Endpoint("users")
public class UsersController extends EntityEndpointBase<User, UsersRepository, UserRequest> {
	public UsersController(UsersRepository repo) {
		super(repo);
	}
}
