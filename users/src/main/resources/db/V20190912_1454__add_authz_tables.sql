CREATE TABLE authz_users
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	username VARCHAR(50) NOT NULL UNIQUE,
	password VARCHAR(100) NOT NULL,
	disabled BOOLEAN NOT NULL DEFAULT false,

	PRIMARY KEY(id)
);

CREATE TABLE authz_roles
(
	id UUID NOT NULL DEFAULT gen_random_uuid(),
	created_at TIMESTAMP NOT NULL DEFAULT now(),
	updated_at TIMESTAMP NOT NULL DEFAULT now(),

	username VARCHAR(50) NOT NULL,
	rolename VARCHAR(50) NOT NULL,

	PRIMARY KEY(id),
	FOREIGN KEY(username) REFERENCES authz_users(username)
);
