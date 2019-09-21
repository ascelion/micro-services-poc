CREATE TABLE products_users
(
	username VARCHAR(50) NOT NULL PRIMARY KEY,
	password VARCHAR(100) NOT NULL,
	disabled BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE products_roles
(
	username VARCHAR(50) NOT NULL,
	rolename VARCHAR(50) NOT NULL,
	FOREIGN KEY (username) REFERENCES products_users(username)
);
 
CREATE UNIQUE INDEX ix_users_roles
	ON products_roles (username, rolename);

INSERT INTO products_users(username, password)
	VALUES( 'admin', '$2a$10$6Uil0XnU8jCorqwtZR4cNe/QXy2LBz6kxLLY3VxENelBgAgRt1/Zu'); -- 'adminadmin'
INSERT INTO products_roles(username, rolename) values ('admin', 'ADMIN');
INSERT INTO products_roles(username, rolename) values ('admin', 'USER');

INSERT INTO products_users(username, password)
	VALUES( 'user', '$2a$10$VBzZ6nGSMxwTJKp7Tk6tNegPt1Yq2AXNsf/invjEJq2ECfEm01t9C'); -- 'useruser'
INSERT INTO products_roles(username, rolename) values ('user', 'USER');
