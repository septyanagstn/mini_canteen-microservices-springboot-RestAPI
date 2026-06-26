DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(100) UNIQUE NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    customer_email VARCHAR(255) NOT NULL, 
    total_amount NUMERIC(12, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT chk_order_status CHECK (status IN ('PENDING', 'PAID', 'CANCELLED'))
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), 
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    product_sku VARCHAR(50) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_price NUMERIC(12, 2) NOT NULL,
    quantity INT NOT NULL,
    
    -- Constraints
    CONSTRAINT fk_order_parent FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT chk_quantity_min CHECK (quantity >= 1)
);