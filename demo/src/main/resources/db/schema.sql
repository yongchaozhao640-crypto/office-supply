SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS office_supply (
    id INT AUTO_INCREMENT PRIMARY KEY,
    supply_no INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    unit VARCHAR(20),
    model VARCHAR(50),
    unit_price DECIMAL(10,2),
    warning_stock DECIMAL(10,2) DEFAULT 5,
    remark VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS department (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dept_no INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    remark VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS personnel (
    id INT AUTO_INCREMENT PRIMARY KEY,
    person_no INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    dept_name VARCHAR(100),
    remark VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS purchase_record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    purchase_no INT NOT NULL,
    purchase_date DATE,
    purchaser VARCHAR(50),
    supply_id INT,
    supply_name VARCHAR(100),
    unit VARCHAR(20),
    model VARCHAR(50),
    unit_price DECIMAL(10,2),
    quantity DECIMAL(10,2),
    amount DECIMAL(12,2),
    remark VARCHAR(200),
    year INT,
    month INT,
    INDEX idx_purchase_supply_id (supply_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS usage_record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usage_no INT NOT NULL,
    usage_date DATE,
    person_name VARCHAR(50),
    dept_name VARCHAR(100),
    supply_id INT,
    supply_name VARCHAR(100),
    unit VARCHAR(20),
    model VARCHAR(50),
    unit_price DECIMAL(10,2),
    quantity DECIMAL(10,2),
    amount DECIMAL(12,2),
    remark VARCHAR(200),
    year INT,
    month INT,
    INDEX idx_usage_supply_id (supply_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS inventory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    supply_no INT NOT NULL,
    supply_name VARCHAR(100),
    unit VARCHAR(20),
    model VARCHAR(50),
    unit_price DECIMAL(10,2),
    opening_qty DECIMAL(10,2) DEFAULT 0,
    opening_amount DECIMAL(12,2) DEFAULT 0,
    current_qty DECIMAL(10,2) DEFAULT 0,
    current_amount DECIMAL(12,2) DEFAULT 0,
    safety_stock DECIMAL(10,2) DEFAULT 0,
    need_purchase VARCHAR(10),
    version INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
