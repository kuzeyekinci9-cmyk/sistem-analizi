CREATE DATABASE IF NOT EXISTS site_management;
USE site_management;

-- Başka bilgisayarlara kurarken hata almamak için Foreign Key kontrollerini geçici olarak kapatıyoruz
SET FOREIGN_KEY_CHECKS = 0;

-- Eski tabloları temizle
DROP TABLE IF EXISTS Vehicle_Logs;
DROP TABLE IF EXISTS Poll_Votes;
DROP TABLE IF EXISTS Poll_Options;
DROP TABLE IF EXISTS Polls;
DROP TABLE IF EXISTS Transactions;
DROP TABLE IF EXISTS Maintenance_Tickets;
DROP TABLE IF EXISTS Vehicles;
DROP TABLE IF EXISTS Guest_Passes;
DROP TABLE IF EXISTS Site_Finances;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Apartments;
DROP TABLE IF EXISTS Announcements;

-- 1. Önce Users (Sakinler/Personel) tablosu (Apartments'dan önce oluşturulmalı çünkü foreign key oradan gelecek)
CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, 
    dues_debt DECIMAL(10,2) DEFAULT 0.00,
    extra_debt DECIMAL(10,2) DEFAULT 0.00
);

-- 2. Apartments tablosu (resident_id buraya eklendi)
CREATE TABLE Apartments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT, -- Dairenin muhatabı/sahibi olan kişi
    block_name VARCHAR(10),
    floor_number INT,
    door_number INT,
    headcount INT DEFAULT 0,
    is_occupied BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (resident_id) REFERENCES Users(id) ON DELETE SET NULL
);



-- 3. Araçlar tablosu (Doğrudan daireye bağlı)
CREATE TABLE Vehicles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    apartment_id INT,
    license_plate VARCHAR(20) NOT NULL,
    FOREIGN KEY (apartment_id) REFERENCES Apartments(id) ON DELETE CASCADE
);

-- 4. Arıza Kayıtları tablosu
CREATE TABLE Maintenance_Tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT,
    title VARCHAR(255),
    description TEXT,
    status VARCHAR(50) DEFAULT 'OPEN', 
    assigned_staff_id INT,  
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_staff_id) REFERENCES Users(id) ON DELETE SET NULL
);

-- 5. Finans/İşlem Geçmişi tablosu
CREATE TABLE Transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT,
    amount DECIMAL(10,2) NOT NULL,
    transaction_type VARCHAR(50), 
    description VARCHAR(255),
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES Users(id) ON DELETE CASCADE
);

-- 6. Site Genel Finans (Gelir-Gider) tablosu
CREATE TABLE Site_Finances (
    id INT AUTO_INCREMENT PRIMARY KEY,
    finance_type VARCHAR(50),
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 7. Misafir Araç Geçişleri tablosu
CREATE TABLE Guest_Passes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT,
    license_plate VARCHAR(20) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resident_id) REFERENCES Users(id) ON DELETE CASCADE
);

-- 8. Anketler tablosu
CREATE TABLE Polls (
    id INT AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(255) NOT NULL
);

-- 9. Anket Seçenekleri tablosu
CREATE TABLE Poll_Options (
    id INT AUTO_INCREMENT PRIMARY KEY,
    poll_id INT,
    option_text VARCHAR(100),
    vote_count INT DEFAULT 0,
    FOREIGN KEY (poll_id) REFERENCES Polls(id) ON DELETE CASCADE
);

-- 10. Kullanılan Oyların Kayıt tablosu
CREATE TABLE Poll_Votes (
    poll_id INT,
    resident_id INT,
    PRIMARY KEY (poll_id, resident_id), 
    FOREIGN KEY (poll_id) REFERENCES Polls(id) ON DELETE CASCADE,
    FOREIGN KEY (resident_id) REFERENCES Users(id) ON DELETE CASCADE
);

-- 11. Otopark Giriş-Çıkış Kayıtları (Log)
CREATE TABLE Vehicle_Logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20),
    is_guest BOOLEAN,
    entry_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    exit_time DATETIME NULL
);

-- 12. Duyurular tablosu
CREATE TABLE Announcements (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    publish_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Foreign Key kontrollerini tekrar aktif ediyoruz
SET FOREIGN_KEY_CHECKS = 1;

-- Varsayılan Admin Hesabını Sistemi Kurarken Otomatik Olarak Ekle (İsteğe Bağlı Kolaylık)
INSERT INTO Users (full_name, phone, password, role) VALUES ('Sistem Yöneticisi', 'admin', 'admin', 'ADMIN');
