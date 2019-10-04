package ascelion.micro.basket;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BasketItemRequest {
	private final UUID productId;
	private final BigDecimal quantity;
}
