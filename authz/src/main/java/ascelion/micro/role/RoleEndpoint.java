package ascelion.micro.role;

import ascelion.micro.shared.endpoint.Endpoint;
import ascelion.micro.shared.endpoint.ViewEntityEndpointBase;

@Endpoint("roles")
public class RoleEndpoint extends ViewEntityEndpointBase<Role, RoleRepo> {
	public RoleEndpoint(RoleRepo repo) {
		super(repo);
	}
}
