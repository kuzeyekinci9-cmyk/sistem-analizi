CREATE DATABASE IF NOT EXISTS site_management;
USE site_management;

DROP TABLE IF EXISTS Vehicle_Logs;
DROP TABLE IF EXISTS Poll_Votes;
DROP TABLE IF EXISTS Poll_Options;
DROP TABLE IF EXISTS Polls;
DROP TABLE IF EXISTS Transactions;
DROP TABLE IF EXISTS Maintenance_Tickets;
DROP TABLE IF EXISTS Vehicles;
DROP TABLE IF EXISTS Apartments;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, 
    dues_debt DECIMAL(10,2) DEFAULT 0.00,
    extra_debt DECIMAL(10,2) DEFAULT 0.00
);

CREATE TABLE Apartments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT, 
    block_name VARCHAR(10),
    floor_number INT,
    door_number INT,
    headcount INT DEFAULT 0,
    is_occupied BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (resident_id) REFERENCES Users(id) ON DELETE SET NULL
);

CREATE TABLE Vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    apartment_id INT,
    license_plate VARCHAR(20) NOT NULL,
    FOREIGN KEY (apartment_id) REFERENCES Apartments(id) ON DELETE CASCADE
);

CREATE TABLE Maintenance_Tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT,
    title VARCHAR(255),
    description TEXT,
    status VARCHAR(50) DEFAULT 'OPEN', 
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES Users(id) ON DELETE CASCADE
);

CREATE TABLE Transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT,
    amount DECIMAL(10,2) NOT NULL,
    transaction_type VARCHAR(50), 
    description VARCHAR(255),
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES Users(id) ON DELETE CASCADE
);

-- Polls System (3 Tables)
CREATE TABLE Polls (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(255) NOT NULL
);

CREATE TABLE Poll_Options (
    id INT AUTO_INCREMENT PRIMARY KEY,
    poll_id INT,
    option_text VARCHAR(100),
    vote_count INT DEFAULT 0,
    FOREIGN KEY (poll_id) REFERENCES Polls(id) ON DELETE CASCADE
);

CREATE TABLE Poll_Votes (
    poll_id INT,
    resident_id INT,
    PRIMARY KEY (poll_id, resident_id), 
    FOREIGN KEY (poll_id) REFERENCES Polls(id) ON DELETE CASCADE,
    FOREIGN KEY (resident_id) REFERENCES Users(id) ON DELETE CASCADE
);

-- Vehicle Logs (Parking Entry/Exit)
CREATE TABLE Vehicle_Logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20),
    is_guest BOOLEAN,
    entry_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    exit_time DATETIME NULL
);