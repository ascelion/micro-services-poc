ALTER TABLE customers ADD COLUMN email VARCHAR(80) NOT NULL DEFAULT '';
UPDATE customers c SET email = c.first_name || '.' || c.last_name || '@gmail.com';
ALTER TABLE customers ALTER COLUMN email DROP DEFAULT;
