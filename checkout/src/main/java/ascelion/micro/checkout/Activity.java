package ascelion.micro.checkout;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Activity {
	private final String id;
	private final String name;
	private final String type;
}
