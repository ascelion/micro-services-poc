ALTER TABLE payments ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT '';
UPDATE payments p SET status = 'APPROVED' WHERE p.approved = TRUE;
UPDATE payments p SET status = 'WAITING' WHERE p.approved <> TRUE;
ALTER TABLE payments ALTER COLUMN status DROP DEFAULT;
ALTER TABLE payments DROP COLUMN approved;

