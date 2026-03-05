CREATE DATABASE IF NOT EXISTS company_db;
USE company_db;
-- SAFETY DROPS
DROP TRIGGER IF EXISTS employees_after_insert;
DROP TRIGGER IF EXISTS employees_after_update;
DROP VIEW IF EXISTS daily_activity_report;
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS employees;

-- 1. EMPLOYEES TABLE
CREATE TABLE employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    department VARCHAR(100),
    salary DECIMAL(10,2),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
        ON UPDATE CURRENT_TIMESTAMP
);

-- 2. AUDIT LOG TABLE
CREATE TABLE audit_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    table_name VARCHAR(100),
    operation VARCHAR(20),
    record_id INT,
    old_data JSON,
    new_data JSON,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DELIMITER $$

-- 3. INSERT TRIGGER
CREATE TRIGGER employees_after_insert
AFTER INSERT ON employees
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (
        table_name,
        operation,
        record_id,
        new_data
    )
    VALUES (
        'employees',
        'INSERT',
        NEW.employee_id,
        JSON_OBJECT(
            'employee_id', NEW.employee_id,
            'name', NEW.name,
            'department', NEW.department,
            'salary', NEW.salary,
            'updated_at', CURRENT_TIMESTAMP
        )
    );
END$$

-- 4. UPDATE TRIGGER
CREATE TRIGGER employees_after_update
AFTER UPDATE ON employees
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (
        table_name,
        operation,
        record_id,
        old_data,
        new_data
    )
    VALUES (
        'employees',
        'UPDATE',
        NEW.employee_id,
        JSON_OBJECT(
            'employee_id', OLD.employee_id,
            'name', OLD.name,
            'department', OLD.department,
            'salary', OLD.salary,
            'updated_at', OLD.updated_at
        ),
        JSON_OBJECT(
            'employee_id', NEW.employee_id,
            'name', NEW.name,
            'department', NEW.department,
            'salary', NEW.salary,
            'updated_at', NEW.updated_at
        )
    );
END$$

DELIMITER ;

-- 5. VIEW
CREATE VIEW daily_activity_report AS
SELECT
    DATE(changed_at) AS activity_date,
    table_name,
    operation,
    COUNT(*) AS total_operations
FROM audit_log
GROUP BY
    DATE(changed_at),
    table_name,
    operation
ORDER BY
    activity_date DESC;

-- 6. TEST DATA
INSERT INTO employees (name, department, salary)
VALUES ('Alice', 'IT', 70000);

UPDATE employees
SET salary = 75000
WHERE employee_id = 1;

-- 7. CHECK VIEW
SELECT * FROM daily_activity_report;
