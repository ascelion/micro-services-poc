CREATE TABLE products
(
	id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
	name VARCHAR(250) NOT NULL UNIQUE,
	price DECIMAL(11, 2) NOT NULL,
	updated TIMESTAMP NOT NULL DEFAULT now()
);
