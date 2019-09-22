CREATE TABLE customers
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	first_name VARCHAR(250) NOT NULL,
	last_name VARCHAR(250) NOT NULL,

	PRIMARY KEY(id)
);
