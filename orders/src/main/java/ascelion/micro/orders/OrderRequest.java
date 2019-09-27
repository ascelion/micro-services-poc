package ascelion.micro.orders;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OrderRequest {
	private final UUID customerId;
	private final UUID deliveryAddressId;
	private final UUID billingAddressId;
	private final List<OrderItemRequest> items;
}
