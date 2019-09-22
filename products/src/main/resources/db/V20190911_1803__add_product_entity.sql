CREATE TABLE products
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	name VARCHAR(250) NOT NULL UNIQUE,
	price DECIMAL(11, 2) NOT NULL,

	PRIMARY KEY(id)
);
