package ascelion.micro.roles;

import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.ViewEntityEndpointBase;

@Endpoint("roles")
public class RolesController extends ViewEntityEndpointBase<Role, RolesRepository> {
	public RolesController(RolesRepository repo) {
		super(repo);
	}
}
