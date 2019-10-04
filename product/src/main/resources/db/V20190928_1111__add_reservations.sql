CREATE TABLE reservations
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	product_id UUID NOT NULL,
	owner_id UUID NOT NULL,
	quantity DECIMAL(11, 2) NOT NULL,

	UNIQUE(product_id, owner_id),
	FOREIGN KEY(product_id) REFERENCES products(id),

	PRIMARY KEY(id)
);
