package ascelion.micro.user;

import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.EntityEndpointBase;

@Endpoint("users")
public class UserEndpoint extends EntityEndpointBase<User, UserRepo, UserRequest> {
	public UserEndpoint(UserRepo repo) {
		super(repo);
	}
}
