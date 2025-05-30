-- Categories
INSERT INTO categories (name) VALUES ('Електроніка') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Одяг') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Продукти') ON CONFLICT (name) DO NOTHING;

-- Products (linking to categories)
INSERT INTO products (name, price, producer, country_of_origin, weight, description, category_id)
SELECT 'Смартфон XYZ', 12500.00, 'TechCorp', 'Китай', 0.2, 'Потужний смартфон з відмінною камерою.', c.id FROM categories c WHERE c.name = 'Електроніка'
ON CONFLICT (name) DO NOTHING;

INSERT INTO products (name, price, producer, country_of_origin, weight, description, category_id)
SELECT 'Футболка Бавовняна', 450.50, 'FashionWear', 'Туреччина', 0.15, 'Легка та зручна бавовняна футболка.', c.id FROM categories c WHERE c.name = 'Одяг'
ON CONFLICT (name) DO NOTHING;

INSERT INTO products (name, price, producer, country_of_origin, weight, description, category_id)
SELECT 'Молоко 3.2%', 35.00, 'Ранкова Свіжість', 'Україна', 1.0, 'Натуральне молоко від локального виробника.', c.id FROM categories c WHERE c.name = 'Продукти'
ON CONFLICT (name) DO NOTHING;

INSERT INTO products (name, price, producer, country_of_origin, weight, description, category_id)
SELECT 'Ноутбук GamingPro', 35000.00, 'GameTech', 'Тайвань', 2.5, 'Ігровий ноутбук високої продуктивності.', c.id FROM categories c WHERE c.name = 'Електроніка'
ON CONFLICT (name) DO NOTHING;

-- Stores
INSERT INTO stores (name, location) VALUES ('ТехноСвіт', 'Київ, вул. Хрещатик, 10') ON CONFLICT (name) DO NOTHING;
INSERT INTO stores (name, location) VALUES ('Стильний Вибір', 'Львів, пл. Ринок, 5') ON CONFLICT (name) DO NOTHING;
INSERT INTO stores (name, location) VALUES ('АТБ Маркет', 'Одеса, вул. Дерибасівська, 1') ON CONFLICT (name) DO NOTHING;

-- Customers
INSERT INTO customers (name, email, phone_number, password) VALUES ('Іван Петров', 'ivan.petrov@example.com', '0981234567', 'password123') ON CONFLICT (email) DO NOTHING;
INSERT INTO customers (name, email, phone_number, password) VALUES ('Марія Іваненко', 'maria.ivanenko@example.com', '0509876543', 'securepass') ON CONFLICT (email) DO NOTHING;

-- Link products to stores (assuming IDs are 1, 2, 3 for products and stores)
-- You'll need to know the actual IDs generated after initial insertion.
-- This part is more tricky with `ON CONFLICT DO NOTHING` as IDs might vary.
-- A more robust way would be to fetch IDs. For demonstration, let's try.

-- Fetch IDs for products and stores
-- SELECT id FROM products WHERE name = 'Смартфон XYZ';
-- SELECT id FROM products WHERE name = 'Футболка Бавовняна';
-- SELECT id FROM products WHERE name = 'Молоко 3.2%';
-- SELECT id FROM stores WHERE name = 'ТехноСвіт';
-- SELECT id FROM stores WHERE name = 'Стильний Вибір';
-- SELECT id FROM stores WHERE name = 'АТБ Маркет';

-- Example insertions for store_products (you might need to adjust IDs after your first run)
-- INSERT INTO store_products (store_id, product_id) VALUES (1, 1); -- ТехноСвіт sells Смартфон XYZ
-- INSERT INTO store_products (store_id, product_id) VALUES (1, 4); -- ТехноСвіт sells Ноутбук GamingPro
-- INSERT INTO store_products (store_id, product_id) VALUES (2, 2); -- Стильний Вибір sells Футболка Бавовняна
-- INSERT INTO store_products (store_id, product_id) VALUES (3, 3); -- АТБ Маркет sells Молоко 3.2%
-- INSERT INTO store_products (store_id, product_id) VALUES (3, 1); -- АТБ Маркет also sells Смартфон XYZ (example)

-- To make initial ManyToMany associations work with `data.sql` and `ON CONFLICT`,
-- it's usually better to have more explicit `SELECT id FROM ...` subqueries.
-- For simplicity, let's assume direct IDs for a first run where tables are empty.
-- If you run this multiple times, adjust or manually insert these.

-- For store_products, it's safer to use subqueries or run this manually after initial data:
INSERT INTO store_products (store_id, product_id)
SELECT s.id, p.id FROM stores s, products p
WHERE s.name = 'ТехноСвіт' AND p.name = 'Смартфон XYZ'
ON CONFLICT DO NOTHING;

INSERT INTO store_products (store_id, product_id)
SELECT s.id, p.id FROM stores s, products p
WHERE s.name = 'ТехноСвіт' AND p.name = 'Ноутбук GamingPro'
ON CONFLICT DO NOTHING;

INSERT INTO store_products (store_id, product_id)
SELECT s.id, p.id FROM stores s, products p
WHERE s.name = 'Стильний Вибір' AND p.name = 'Футболка Бавовняна'
ON CONFLICT DO NOTHING;

INSERT INTO store_products (store_id, product_id)
SELECT s.id, p.id FROM stores s, products p
WHERE s.name = 'АТБ Маркет' AND p.name = 'Молоко 3.2%'
ON CONFLICT DO NOTHING;

INSERT INTO store_products (store_id, product_id)
SELECT s.id, p.id FROM stores s, products p
WHERE s.name = 'АТБ Маркет' AND p.name = 'Смартфон XYZ'
ON CONFLICT DO NOTHING;

-- Product Image URLs
INSERT INTO product_image_urls (product_id, image_url)
SELECT p.id, 'https://example.com/images/smartphone_xyz_1.jpg' FROM products p WHERE p.name = 'Смартфон XYZ' ON CONFLICT DO NOTHING;
INSERT INTO product_image_urls (product_id, image_url)
SELECT p.id, 'https://example.com/images/smartphone_xyz_2.jpg' FROM products p WHERE p.name = 'Смартфон XYZ' ON CONFLICT DO NOTHING;

INSERT INTO product_image_urls (product_id, image_url)
SELECT p.id, 'https://example.com/images/tshirt_1.jpg' FROM products p WHERE p.name = 'Футболка Бавовняна' ON CONFLICT DO NOTHING;

INSERT INTO product_image_urls (product_id, image_url)
SELECT p.id, 'https://example.com/images/milk_1.jpg' FROM products p WHERE p.name = 'Молоко 3.2%' ON CONFLICT DO NOTHING;

-- Purchases (example)
INSERT INTO purchases (customer_id, product_id, purchase_date)
SELECT c.id, p.id, NOW() FROM customers c, products p
WHERE c.email = 'ivan.petrov@example.com' AND p.name = 'Смартфон XYZ';

INSERT INTO purchases (customer_id, product_id, purchase_date)
SELECT c.id, p.id, NOW() FROM customers c, products p
WHERE c.email = 'maria.ivanenko@example.com' AND p.name = 'Футболка Бавовняна';