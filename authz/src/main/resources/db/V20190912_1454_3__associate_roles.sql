
INSERT INTO authz_users_roles(user_id, role_id)
VALUES( (SELECT id FROM authz_users WHERE username = 'root'),
		(SELECT id FROM authz_roles WHERE rolename = 'ROOT'));

INSERT INTO authz_users_roles(user_id, role_id)
VALUES( (SELECT id FROM authz_users WHERE username = 'admin'),
		(SELECT id FROM authz_roles WHERE rolename = 'ADMIN'));

INSERT INTO authz_users_roles(user_id, role_id)
VALUES( (SELECT id FROM authz_users WHERE username = 'admin'),
		(SELECT id FROM authz_roles WHERE rolename = 'USER'));

INSERT INTO authz_users_roles(user_id, role_id)
VALUES( (SELECT id FROM authz_users WHERE username = 'user'),
		(SELECT id FROM authz_roles WHERE rolename = 'USER'));
