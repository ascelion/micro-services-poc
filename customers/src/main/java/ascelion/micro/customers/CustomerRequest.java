package ascelion.micro.customers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * DTO used by the endpoint.
 */
@RequiredArgsConstructor
@Getter
public class CustomerRequest {
	private final String firstName;
	private final String lastName;
}
