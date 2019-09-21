CREATE USER @application@ WITH PASSWORD '@application@';
CREATE DATABASE @application@ ENCODING 'UTF-8' OWNER @application@;
\c @application@
ALTER SCHEMA public OWNER TO @application@;
CREATE SCHEMA @application@ AUTHORIZATION @application@;
