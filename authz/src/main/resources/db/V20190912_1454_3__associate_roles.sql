
INSERT INTO authz_users_roles(user_id, role_id)
VALUES( (SELECT id FROM authz_users WHERE username = 'admin'),
		(SELECT id FROM authz_roles WHERE rolename = 'ADMINS'));

INSERT INTO authz_users_roles(user_id, role_id)
VALUES( (SELECT id FROM authz_users WHERE username = 'admin'),
		(SELECT id FROM authz_roles WHERE rolename = 'USERS'));

INSERT INTO authz_users_roles(user_id, role_id)
VALUES( (SELECT id FROM authz_users WHERE username = 'user'),
		(SELECT id FROM authz_roles WHERE rolename = 'USERS'));
