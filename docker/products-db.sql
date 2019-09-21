CREATE USER products WITH PASSWORD 'products';
CREATE DATABASE products ENCODING 'UTF-8' OWNER products;
\c products
ALTER SCHEMA public OWNER TO products;
CREATE SCHEMA products AUTHORIZATION products;
