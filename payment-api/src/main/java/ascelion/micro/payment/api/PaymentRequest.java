package ascelion.micro.payment.api;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentRequest implements Serializable {
	public final String card;
	public final BigDecimal amount;
}
