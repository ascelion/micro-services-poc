-- any product should have a description
ALTER TABLE products ADD COLUMN description TEXT NOT NULL DEFAULT '';
