package ascelion.micro.roles;

import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.ViewEntityEndpoint;

@Endpoint("roles")
public class RolesController extends ViewEntityEndpoint<Role> {
	public RolesController(RolesRepository repo) {
		super(repo);
	}
}
