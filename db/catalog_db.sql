DROP TABLE IF EXISTS products;

CREATE TABLE products ( 
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), 
    sku VARCHAR(50) UNIQUE NOT NULL, 
    name VARCHAR(255) NOT NULL, 
    price NUMERIC(12, 2) NOT NULL, 
    stock INT NOT NULL DEFAULT 0, 
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP 
);;

-- SEEDER
INSERT INTO products (sku, name, price, stock, status) VALUES
('KAMPUS-001', 'Jaket Himpunan JTK', 150000.00, 10, 'ACTIVE'),
('KAMPUS-002', 'Kaos Polban Gajah', 85000.00, 5, 'ACTIVE'),
('KAMPUS-003', 'Gantungan Kunci JTK', 15000.00, 0, 'ACTIVE'), 
('KAMPUS-004', 'Sticker JTK Glossy', 5000.00, 100, 'INACTIVE');  