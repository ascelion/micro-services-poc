package ascelion.micro.roles;

import ascelion.micro.endpoint.Endpoint;
import ascelion.micro.endpoint.ViewEntityEndpoint;

@Endpoint("roles")
public class RolesController extends ViewEntityEndpoint<Role> {
	public RolesController(RolesRepository repo) {
		super(repo);
	}
}
