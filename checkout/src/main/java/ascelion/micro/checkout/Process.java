package ascelion.micro.checkout;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Process {
	private final String id;
	private final String state;
	private final List<Activity> activities;
}
