CREATE TABLE customers
(
	id BIGINT NOT NULL DEFAULT nextval('hibernate_sequence'),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),
	first_name VARCHAR(250) NOT NULL,
	last_name VARCHAR(250) NOT NULL,
	PRIMARY KEY(id)
);
