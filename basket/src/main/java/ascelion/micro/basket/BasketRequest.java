package ascelion.micro.basket;

import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BasketRequest {
	private final UUID customerId;
}
