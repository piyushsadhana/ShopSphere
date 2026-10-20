-- =====================================================================
-- ShopSphere E-Commerce Complete Database Schema & Seed Data
-- Database Engine: MySQL 8.x
-- Character Set: utf8mb4 / Collation: utf8mb4_unicode_ci
-- =====================================================================

CREATE DATABASE IF NOT EXISTS shopsphere
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE shopsphere;

-- Temporarily disable foreign key checks for safe clean re-creation
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- 1. DROP EXISTING TABLES
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS wishlist;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS coupons;
DROP TABLE IF EXISTS coupon_usage;
DROP TABLE IF EXISTS users;

-- ---------------------------------------------------------------------
-- 2. USERS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('CUSTOMER', 'ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 3. CATEGORIES TABLE
-- ---------------------------------------------------------------------
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(255),
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category_status (status)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 4. PRODUCTS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(200) NOT NULL,
    brand VARCHAR(100),
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    discount DECIMAL(5,2) DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0,
    image_url VARCHAR(255),
    status ENUM('ACTIVE', 'INACTIVE', 'ARCHIVED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT,
    INDEX idx_product_category (category_id),
    INDEX idx_product_status (status),
    INDEX idx_product_price (price),
    INDEX idx_product_brand (brand)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 5. ADDRESSES TABLE
-- ---------------------------------------------------------------------
CREATE TABLE addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL DEFAULT 'India',
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_address_user (user_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 6. CART ITEMS TABLE (Persistent DB Cart)
-- ---------------------------------------------------------------------
CREATE TABLE cart_items (
    cart_item_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_cart_product (user_id, product_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 7. ORDERS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    address_snapshot TEXT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    shipping_fee DECIMAL(10,2) DEFAULT 0.00,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    final_amount DECIMAL(10,2) NOT NULL,
    order_status ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    payment_status ENUM('PENDING', 'PAID', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50) NOT NULL,
    coupon_code VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_order_user (user_id),
    INDEX idx_order_status (order_status),
    INDEX idx_order_created (created_at)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 8. ORDER ITEMS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    product_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    INDEX idx_order_item_order (order_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 9. PAYMENTS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(50) NOT NULL,
    status ENUM('PENDING', 'SUCCESS', 'FAILED') NOT NULL DEFAULT 'PENDING',
    transaction_ref VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    INDEX idx_payment_order (order_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 10. WISHLIST TABLE
-- ---------------------------------------------------------------------
CREATE TABLE wishlist (
    wishlist_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE KEY uk_wishlist_user_prod (user_id, product_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 11. REVIEWS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_product_review (user_id, product_id),
    INDEX idx_review_product (product_id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- 12. COUPONS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE coupons (
    coupon_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    discount_type ENUM('PERCENTAGE', 'FIXED') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_value DECIMAL(10,2) DEFAULT 0.00,
    usage_limit INT DEFAULT 100,
    used_count INT DEFAULT 0,
    expiry_date DATE NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'EXPIRED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_coupon_code (code),
    INDEX idx_coupon_status (status)
) ENGINE=InnoDB;


-- =====================================================================
-- SEED DATA (INITIAL SAMPLE DATA FOR TESTING)
-- =====================================================================

-- Users (Demo Admin: Admin@123 | Demo Customer: Customer@123)
INSERT INTO users (user_id, name, email, password, phone, role, status) VALUES
(1, 'ShopSphere Administrator', 'admin@shopsphere.com', '$2a$10$yDajHaRdHJ1axbWMBQ3lzOAAD.ioCx.UyNqbfuIorIhScwnhVOKPe', '+91 9876543210', 'ADMIN', 'ACTIVE'),
(2, 'John Doe', 'john@example.com', '$2a$10$IVlLoxPus9DU5ypOrlayl.MsN9xa3vLJCHf4A201jhsLxo2d4GlyO', '+91 9123456780', 'CUSTOMER', 'ACTIVE'),
(3, 'Sarah Connor', 'sarah@example.com', '$2a$10$IVlLoxPus9DU5ypOrlayl.MsN9xa3vLJCHf4A201jhsLxo2d4GlyO', '+91 9988776655', 'CUSTOMER', 'ACTIVE');

-- Categories
INSERT INTO categories (category_id, name, description, image_url, status) VALUES
(1, 'Electronics & Gadgets', 'Smartphones, laptops, smart accessories, and cutting-edge audio', 'images/cat-electronics.svg', 'ACTIVE'),
(2, 'Fashion & Apparel', 'Men and women trendy clothing, footwear, and designer wear', 'images/cat-fashion.svg', 'ACTIVE'),
(3, 'Home & Living', 'Modern kitchenware, cozy home decor, and contemporary furniture', 'images/cat-home.svg', 'ACTIVE'),
(4, 'Books & Stationery', 'Bestselling novels, educational resources, and premium stationery', 'images/cat-books.svg', 'ACTIVE'),
(5, 'Health & Personal Care', 'Skincare, wellness essentials, fitness equipment, and grooming', 'images/cat-beauty.svg', 'ACTIVE');

-- Products
INSERT INTO products (product_id, category_id, name, brand, description, price, discount, stock, image_url, status) VALUES
(1, 1, 'Pro Wireless Noise-Cancelling Headphones', 'SoundMax', 'Premium over-ear wireless headphones with active noise cancellation, 40-hour battery life, and crystal-clear Hi-Res audio.', 12999.00, 15.00, 35, 'images/products/headphones.svg', 'ACTIVE'),
(2, 1, 'Ultra Slim 4K UHD Smart OLED TV 55"', 'VisionPlus', 'Stunning 55-inch OLED display featuring HDR10+, Dolby Vision Atmos sound, and AI picture optimization.', 54999.00, 10.00, 12, 'images/products/smart-tv.svg', 'ACTIVE'),
(3, 1, 'Titanium Smartwatch Pro GPS', 'ApexPulse', 'Next-gen fitness smartwatch with heart rate monitoring, SpO2 sensor, offline maps, and waterproof titanium build.', 8499.00, 20.00, 50, 'images/products/smartwatch.svg', 'ACTIVE'),
(4, 1, 'UltraBook Neo 14" M3 Processor 16GB/512GB', 'SpeedCraft', 'Lightweight aluminium laptop with vibrant Retina-grade screen, backlit keyboard, and all-day 18-hour battery.', 78999.00, 8.00, 20, 'images/products/laptop.svg', 'ACTIVE'),

(5, 2, 'Classic Tailored Oxford Cotton Shirt', 'UrbanThread', '100% Egyptian combed cotton casual-formal shirt with breathable weave and wrinkle-resistant finish.', 1899.00, 10.00, 75, 'images/products/shirt.svg', 'ACTIVE'),
(6, 2, 'Slim Fit Stretch Denim Jeans', 'RawDenim Co.', 'Premium durable denim featuring comfortable flex technology and timeless vintage wash.', 2499.00, 25.00, 60, 'images/products/jeans.svg', 'ACTIVE'),
(7, 2, 'Performance Cushion Running Sneakers', 'AeroStep', 'Ergonomic breathable mesh athletic trainers with responsive air-cushion sole for marathon endurance.', 3999.00, 30.00, 45, 'images/products/sneakers.svg', 'ACTIVE'),
(8, 2, 'All-Weather Windproof Travel Jacket', 'NorthRidge', 'Lightweight, water-resistant hooded jacket with thermal lining and concealed utility pockets.', 4599.00, 15.00, 28, 'images/products/jacket.svg', 'ACTIVE'),

(9, 3, 'Smart Programmable Espresso & Coffee Machine', 'BaristaCraft', '15-bar Italian pump espresso maker with integrated milk frother and precision temperature control.', 14999.00, 12.00, 18, 'images/products/coffee-maker.svg', 'ACTIVE'),
(10, 3, 'Ergonomic Lumbar Support Office Chair', 'PostureFit', 'Mesh executive desk chair with 3D adjustable armrests, adaptive tilt, and reinforced steel base.', 11499.00, 18.00, 22, 'images/products/office-chair.svg', 'ACTIVE'),
(11, 3, 'Stainless Steel Chef Knife Set 6-Piece', 'EdgeMaster', 'Hand-forged German high-carbon stainless steel knife block set with ergonomic pakkawood handles.', 3499.00, 20.00, 40, 'images/products/knife-set.svg', 'ACTIVE'),

(12, 4, 'Atomic Habits: Proven Framework for Change', 'Penguin Life', 'Groundbreaking bestseller by James Clear detailing practical strategies for lasting behavioral improvement.', 599.00, 25.00, 120, 'images/products/book-habits.svg', 'ACTIVE'),
(13, 4, 'Designing Data-Intensive Applications', 'O Reilly', 'The definitive guide to distributed data systems, storage engines, and scalable software architectures.', 1850.00, 10.00, 30, 'images/products/book-system.svg', 'ACTIVE'),

(14, 5, 'Sonic Clean Electric Toothbrush with UV Sanitizer', 'DentaGlow', '40,000 vibrations/min ultrasonic toothbrush with 4 cleaning modes and smart 2-minute timer.', 2899.00, 15.00, 65, 'images/products/toothbrush.svg', 'ACTIVE'),
(15, 5, 'Organic Botanical Vitamin C Face Serum 50ml', 'PureGlow Organics', 'Enriched with pure hyaluronic acid, niacinamide, and natural antioxidant botanicals for radiant skin.', 999.00, 20.00, 85, 'images/products/face-serum.svg', 'ACTIVE');

-- Addresses
INSERT INTO addresses (address_id, user_id, recipient_name, phone, address_line1, address_line2, city, state, postal_code, country, is_default) VALUES
(1, 2, 'John Doe', '+91 9123456780', 'Flat 402, Sunshine Heights', 'MG Road, Indiranagar', 'Bengaluru', 'Karnataka', '560038', 'India', TRUE),
(2, 2, 'John Doe (Office)', '+91 9123456780', 'Tower B, 6th Floor, Tech Park', 'Outer Ring Road, Bellandur', 'Bengaluru', 'Karnataka', '560103', 'India', FALSE);

-- Coupons
INSERT INTO coupons (coupon_id, code, discount_type, discount_value, min_order_value, usage_limit, used_count, expiry_date, status) VALUES
(1, 'WELCOME10', 'PERCENTAGE', 10.00, 1000.00, 500, 5, '2028-12-31', 'ACTIVE'),
(2, 'SAVE20', 'PERCENTAGE', 20.00, 3000.00, 200, 12, '2028-12-31', 'ACTIVE'),
(3, 'FLAT500', 'FIXED', 500.00, 2500.00, 150, 8, '2028-12-31', 'ACTIVE'),
(4, 'FESTIVE50', 'PERCENTAGE', 50.00, 10000.00, 50, 0, '2028-12-31', 'ACTIVE');

-- Sample Initial Orders
INSERT INTO orders (order_id, order_number, user_id, address_snapshot, total_amount, discount_amount, shipping_fee, tax_amount, final_amount, order_status, payment_status, payment_method, coupon_code, notes) VALUES
(1, 'ORD-20261020-001', 2, 'John Doe, +91 9123456780\nFlat 402, Sunshine Heights, MG Road, Indiranagar\nBengaluru, Karnataka - 560038, India', 12999.00, 1299.90, 0.00, 2105.84, 13804.94, 'DELIVERED', 'PAID', 'Credit / Debit Card (Demo)', 'WELCOME10', 'Please ring the doorbell upon arrival.'),
(2, 'ORD-20261020-002', 2, 'John Doe, +91 9123456780\nFlat 402, Sunshine Heights, MG Road, Indiranagar\nBengaluru, Karnataka - 560038, India', 3999.00, 0.00, 99.00, 719.82, 4817.82, 'SHIPPED', 'PAID', 'Cash on Delivery (COD)', NULL, 'Deliver between 10am-5pm.');

-- Order Items
INSERT INTO order_items (order_item_id, order_id, product_id, product_name, product_price, quantity, subtotal) VALUES
(1, 1, 1, 'Pro Wireless Noise-Cancelling Headphones', 11049.15, 1, 11049.15),
(2, 2, 7, 'Performance Cushion Running Sneakers', 2799.30, 1, 2799.30);

-- Payments
INSERT INTO payments (payment_id, order_id, amount, method, status, transaction_ref) VALUES
(1, 1, 13804.94, 'Credit / Debit Card (Demo)', 'SUCCESS', 'TXN-CARD-99238471'),
(2, 2, 4817.82, 'Cash on Delivery (COD)', 'PENDING', 'TXN-COD-PENDING');

-- Wishlist
INSERT INTO wishlist (user_id, product_id) VALUES
(2, 3),
(2, 4);

-- Reviews
INSERT INTO reviews (review_id, user_id, product_id, rating, review_text) VALUES
(1, 2, 1, 5, 'Unbelievable noise cancelling! Comfortable even for 8-hour flights. Worth every rupee.'),
(2, 3, 1, 4, 'Very good sound stage, bass is tight and punchy. Fast Bluetooth pairing.'),
(3, 2, 7, 5, 'Extremely comfortable running shoes, lightweight and great grip on wet roads.');

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
