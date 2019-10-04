package ascelion.micro.flow;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CheckoutConstants {
	static public final String PROCESS_NAME = "checkout";

	static public final String BASKET_ID_VAR = "basketId";
	static public final String BASKET_RESPONSE_VAR = "basketResponse";
	static public final String CUSTOMER_RESPONSE_VAR = "customerResponse";
	static public final String RESERVATIONS_VAR = "reservations";
	static public final String OPERATION_VAR = "operation";
	static public final String SHIP_ITEMS_RESPONSE_VAR = "shipItemsResponse";

	static public final String BASKET_REQUEST_TASK = "retrieveBasket";
	static public final String BASKET_RECEIVE_TASK = "basketReceive";
	static public final String BASKET_VERIFY_LISTENER = "basketVerify";
	static public final String CUSTOMER_REQUEST_TASK = "retrieveCustomer";
	static public final String CUSTOMER_RECEIVE_TASK = "customerReceive";
	static public final String CUSTOMER_VERIFY_LISTENER = "customerVerify";
	static public final String PAYMENT_REQUEST_TASK = "startPayment";
	static public final String PAYMENT_RECEIVE_TASK = "paymentReceive";
	static public final String PAYMENT_REFUND_TASK = "refundPayment";
	static public final String RESERVATIONS_UPDATE_TASK = "updateReservations";
	static public final String SEND_INVOICE_TASK = "sendInvoice";
	static public final String SHIPPING_REQUEST_TASK = "shipItems";
	static public final String SHIPPING_RECEIVE_TASK = "shippingReceive";
}
