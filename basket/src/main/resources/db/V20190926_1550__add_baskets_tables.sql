CREATE TABLE baskets
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	customer_id UUID NOT NULL UNIQUE,
	status VARCHAR(20) NOT NULL,

	PRIMARY KEY(id)
);

CREATE TABLE basket_items
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	basket_id UUID NOT NULL,
	product_id UUID NOT NULL,
	quantity DECIMAL(9, 2) NOT NULL,
	expired BOOLEAN NOT NULL DEFAULT FALSE,
	ord SMALLINT NOT NULL,

	FOREIGN KEY(basket_id) REFERENCES baskets(id),

	PRIMARY KEY(id)
);
