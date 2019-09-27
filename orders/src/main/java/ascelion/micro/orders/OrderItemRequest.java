package ascelion.micro.orders;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OrderItemRequest {
	private final UUID productId;
	private final BigDecimal quantity;
}
