package ascelion.micro.users;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@RequiredArgsConstructor
public class UserRequest {
	@NotNull
	@Size(min = 6)
	private final String password;
}
