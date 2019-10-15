package ascelion.micro.flow;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckoutConstants {
	static public final String PROCESS_NAME = "checkout";

	static public final String BASKET_RESPONSE_VAR = "basketResponse";
	static public final String CUSTOMER_RESPONSE_VAR = "customerResponse";
	static public final String RESERVATIONS_VAR = "reservations";
	static public final String SHIP_ITEMS_RESPONSE_VAR = "shippingResponse";
	static public final String PAYMENT_RESPONSE_VAR = "paymentResponse";

	static public final String BASKET_STATUS_TASK = "basketStatus";
	static public final String BASKET_RECEIVE_TASK = "basketReceive";
	static public final String CUSTOMER_REQUEST_TASK = "retrieveCustomer";
	static public final String CUSTOMER_RECEIVE_TASK = "customerReceive";
	static public final String PAYMENT_REQUEST_TASK = "startPayment";
	static public final String PAYMENT_RECEIVE_TASK = "paymentReceive";
	static public final String PAYMENT_REFUND_TASK = "refundPayment";
	static public final String RESERVATIONS_UPDATE_TASK = "updateReservations";
	static public final String SEND_INVOICE_TASK = "sendInvoice";
	static public final String SHIPPING_REQUEST_TASK = "shipItems";
	static public final String SHIPPING_RECEIVE_TASK = "shippingReceive";

	static public final String VERIFY_VARIABLE_LISTENER = "verifyVariable";
}
