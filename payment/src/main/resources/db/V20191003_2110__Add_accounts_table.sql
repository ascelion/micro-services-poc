CREATE TABLE accounts
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	customer_id UUID NOT NULL,
	number VARCHAR(24) NOT NULL,
	amount DECIMAL(11, 2) NOT NULL DEFAULT 0,

	PRIMARY KEY(id)
);
