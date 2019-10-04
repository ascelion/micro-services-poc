CREATE TABLE cards
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	account_id UUID NOT NULL,
	number VARCHAR(20) NOT NULL UNIQUE,
	expiration DATE NOT NULL,
	pin VARCHAR(8) NOT NULL,

	FOREIGN KEY(account_id) REFERENCES accounts(id),

	PRIMARY KEY(id)
);
