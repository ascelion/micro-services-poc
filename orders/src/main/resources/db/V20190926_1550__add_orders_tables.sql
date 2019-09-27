CREATE TABLE orders
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	customer_id UUID NOT NULL,
	state VARCHAR(20) NOT NULL,
	delivery_address_id UUID NOT NULL,
	billing_address_id UUID,

	PRIMARY KEY(id)
);

CREATE TABLE order_items
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	order_id UUID NOT NULL,
	product_id UUID NOT NULL,
	quantity DECIMAL(9, 2) NOT NULL,
	ord SMALLINT NOT NULL,

	FOREIGN KEY(order_id) REFERENCES orders(id),

	PRIMARY KEY(id)
);
