CREATE DATABASE IF NOT EXISTS bank_db;
USE bank_db;
DROP TABLE IF EXISTS accounts;

CREATE TABLE accounts (
    account_id INT NOT NULL AUTO_INCREMENT,
    account_type ENUM('USER', 'MERCHANT') NOT NULL,
    balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (account_id)
);
INSERT INTO accounts (account_id, account_type, balance) VALUES
(1, 'USER', 5000.00),
(101, 'MERCHANT', 1000.00);
START TRANSACTION;

-- Lock user row
SELECT balance 
FROM accounts 
WHERE account_id = 1 
FOR UPDATE;

-- Deduct if enough balance
UPDATE accounts
SET balance = balance - 1500
WHERE account_id = 1
  AND balance >= 1500;

-- Check if update happened
SELECT ROW_COUNT() AS affected_rows;

-- Add to merchant
UPDATE accounts
SET balance = balance + 1500
WHERE account_id = 101;

COMMIT;
START TRANSACTION;

UPDATE accounts
SET balance = balance - 7000
WHERE account_id = 1
  AND balance >= 7000;

-- Since no rows updated, rollback
ROLLBACK;
SELECT * FROM accounts;
