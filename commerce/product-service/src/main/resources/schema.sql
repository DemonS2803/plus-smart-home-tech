-- Product Service Schema
-- Database: product_db

CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    price DECIMAL(19, 4) NOT NULL,
    image_url VARCHAR(1000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    category_id BIGINT REFERENCES categories(id)
);

CREATE INDEX IF NOT EXISTS idx_products_category_id ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);

CREATE SEQUENCE IF NOT EXISTS categories_seq INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS products_seq INCREMENT BY 50;
