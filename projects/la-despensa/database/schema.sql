PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS categories (
  id INTEGER PRIMARY KEY,
  slug TEXT NOT NULL UNIQUE,
  name TEXT NOT NULL UNIQUE COLLATE NOCASE,
  description TEXT NOT NULL DEFAULT '',
  CHECK (length(trim(name)) BETWEEN 2 AND 60)
) STRICT;

CREATE TABLE IF NOT EXISTS suppliers (
  id INTEGER PRIMARY KEY,
  name TEXT NOT NULL UNIQUE COLLATE NOCASE,
  email TEXT NOT NULL UNIQUE COLLATE NOCASE,
  CHECK (length(trim(name)) BETWEEN 2 AND 100),
  CHECK (instr(email, '@') > 1)
) STRICT;

CREATE TABLE IF NOT EXISTS products (
  id INTEGER PRIMARY KEY,
  sku TEXT NOT NULL UNIQUE,
  category_id INTEGER NOT NULL,
  name TEXT NOT NULL,
  description TEXT NOT NULL,
  price_cents INTEGER NOT NULL CHECK (price_cents > 0),
  stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
  reorder_level INTEGER NOT NULL DEFAULT 5 CHECK (reorder_level >= 0),
  active INTEGER NOT NULL DEFAULT 1 CHECK (active IN (0, 1)),
  created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
  updated_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
  FOREIGN KEY (category_id) REFERENCES categories(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  CHECK (length(trim(sku)) BETWEEN 3 AND 30),
  CHECK (length(trim(name)) BETWEEN 2 AND 100)
) STRICT;

CREATE TABLE IF NOT EXISTS product_suppliers (
  product_id INTEGER NOT NULL,
  supplier_id INTEGER NOT NULL,
  supplier_sku TEXT NOT NULL,
  unit_cost_cents INTEGER NOT NULL CHECK (unit_cost_cents > 0),
  PRIMARY KEY (product_id, supplier_id),
  UNIQUE (supplier_id, supplier_sku),
  FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON UPDATE CASCADE ON DELETE RESTRICT
) STRICT;

CREATE TABLE IF NOT EXISTS customers (
  id INTEGER PRIMARY KEY,
  name TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE COLLATE NOCASE,
  created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
  CHECK (length(trim(name)) BETWEEN 2 AND 100),
  CHECK (length(email) BETWEEN 6 AND 150),
  CHECK (instr(email, '@') > 1)
) STRICT;

CREATE TABLE IF NOT EXISTS payment_methods (
  id INTEGER PRIMARY KEY,
  code TEXT NOT NULL UNIQUE,
  name TEXT NOT NULL UNIQUE,
  active INTEGER NOT NULL DEFAULT 1 CHECK (active IN (0, 1))
) STRICT;

CREATE TABLE IF NOT EXISTS sales_orders (
  id INTEGER PRIMARY KEY,
  order_number TEXT NOT NULL UNIQUE,
  customer_id INTEGER NOT NULL,
  payment_method_id INTEGER NOT NULL,
  status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'prepared', 'completed', 'cancelled')),
  payment_status TEXT NOT NULL DEFAULT 'pending' CHECK (payment_status IN ('pending', 'paid', 'refunded')),
  created_at TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),
  FOREIGN KEY (customer_id) REFERENCES customers(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id) ON UPDATE CASCADE ON DELETE RESTRICT
) STRICT;

CREATE TABLE IF NOT EXISTS sales_order_items (
  order_id INTEGER NOT NULL,
  product_id INTEGER NOT NULL,
  quantity INTEGER NOT NULL CHECK (quantity BETWEEN 1 AND 99),
  unit_price_cents INTEGER NOT NULL CHECK (unit_price_cents > 0),
  PRIMARY KEY (order_id, product_id),
  FOREIGN KEY (order_id) REFERENCES sales_orders(id) ON UPDATE CASCADE ON DELETE RESTRICT,
  FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE RESTRICT
) STRICT;

CREATE INDEX IF NOT EXISTS idx_products_active_category_name ON products(active, category_id, name COLLATE NOCASE);
CREATE INDEX IF NOT EXISTS idx_products_active_price ON products(active, price_cents);
CREATE INDEX IF NOT EXISTS idx_orders_customer_date ON sales_orders(customer_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_orders_status_date ON sales_orders(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_order_items_product ON sales_order_items(product_id);
CREATE INDEX IF NOT EXISTS idx_product_suppliers_supplier ON product_suppliers(supplier_id);

CREATE VIEW IF NOT EXISTS v_order_totals AS
SELECT
  sales_orders.id,
  sales_orders.order_number,
  sales_orders.customer_id,
  sales_orders.payment_method_id,
  sales_orders.status,
  sales_orders.payment_status,
  sales_orders.created_at,
  COALESCE(SUM(sales_order_items.quantity * sales_order_items.unit_price_cents), 0) AS total_cents,
  COALESCE(SUM(sales_order_items.quantity), 0) AS item_count
FROM sales_orders
LEFT JOIN sales_order_items ON sales_order_items.order_id = sales_orders.id
GROUP BY sales_orders.id;

CREATE VIEW IF NOT EXISTS v_stock_alerts AS
SELECT
  products.id,
  products.sku,
  products.name,
  products.stock,
  products.reorder_level,
  categories.name AS category_name
FROM products
JOIN categories ON categories.id = products.category_id
WHERE products.active = 1 AND products.stock <= products.reorder_level;

CREATE VIEW IF NOT EXISTS v_daily_sales AS
SELECT
  substr(created_at, 1, 10) AS sale_date,
  COUNT(*) AS order_count,
  SUM(total_cents) AS sales_cents
FROM v_order_totals
WHERE status <> 'cancelled'
GROUP BY substr(created_at, 1, 10);

CREATE TRIGGER IF NOT EXISTS trg_order_item_stock_guard
BEFORE INSERT ON sales_order_items
BEGIN
  SELECT CASE
    WHEN COALESCE((SELECT active FROM products WHERE id = NEW.product_id), 0) <> 1
      THEN RAISE(ABORT, 'El producto no está disponible')
    WHEN (SELECT stock FROM products WHERE id = NEW.product_id) < NEW.quantity
      THEN RAISE(ABORT, 'No hay existencias suficientes')
  END;
END;

CREATE TRIGGER IF NOT EXISTS trg_order_item_stock_update
AFTER INSERT ON sales_order_items
BEGIN
  UPDATE products
  SET stock = stock - NEW.quantity,
      updated_at = strftime('%Y-%m-%dT%H:%M:%fZ', 'now')
  WHERE id = NEW.product_id;
END;
