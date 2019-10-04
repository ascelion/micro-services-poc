CREATE TABLE cards
(
	customer_id UUID NOT NULL,
	number VARCHAR(32) NOT NULL,

	FOREIGN KEY(customer_id) REFERENCES customers(id),

	PRIMARY KEY(number)
);
