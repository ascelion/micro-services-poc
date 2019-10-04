CREATE TABLE payments
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	card_id UUID NOT NULL,
	amount DECIMAL(11, 2) NOT NULL,
	request_id UUID NOT NULL,
	approved BOOLEAN NOT NULL DEFAULT FALSE,

	FOREIGN KEY(card_id) REFERENCES cards(id),

	PRIMARY KEY(id)
);
