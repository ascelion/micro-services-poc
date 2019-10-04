CREATE TABLE addresses
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	country VARCHAR(4) NOT NULL,
	region VARCHAR(80),
	locality VARCHAR(80) NOT NULL,
	zip VARCHAR(20) NOT NULL,
	street VARCHAR(80) NOT NULL,
	number VARCHAR(20) NOT NULL,
	extra TEXT,

	PRIMARY KEY(id)
);

CREATE TABLE customers_addresses
(
	customer_id UUID NOT NULL,
	address_id UUID NOT NULL,
	ord SMALLINT,

	UNIQUE(customer_id, address_id),

	FOREIGN KEY(customer_id) REFERENCES customers(id),
	FOREIGN KEY(address_id) REFERENCES addresses(id),

	PRIMARY KEY(customer_id, address_id)
);

