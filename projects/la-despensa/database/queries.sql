SELECT
  categories.name AS categoria,
  COUNT(products.id) AS productos_activos,
  SUM(products.stock) AS unidades_disponibles
FROM categories
LEFT JOIN products ON products.category_id = categories.id AND products.active = 1
GROUP BY categories.id, categories.name
ORDER BY categories.name;

SELECT
  products.sku,
  products.name AS producto,
  suppliers.name AS proveedor,
  product_suppliers.unit_cost_cents AS coste_centimos
FROM product_suppliers
JOIN products ON products.id = product_suppliers.product_id
JOIN suppliers ON suppliers.id = product_suppliers.supplier_id
ORDER BY products.name, suppliers.name;

SELECT
  v_order_totals.order_number,
  customers.name AS cliente,
  payment_methods.name AS metodo_pago,
  v_order_totals.status AS estado,
  v_order_totals.item_count AS unidades,
  v_order_totals.total_cents AS total_centimos
FROM v_order_totals
JOIN customers ON customers.id = v_order_totals.customer_id
JOIN payment_methods ON payment_methods.id = v_order_totals.payment_method_id
ORDER BY v_order_totals.created_at DESC;

SELECT
  customers.name AS cliente,
  COUNT(v_order_totals.id) AS pedidos,
  COALESCE(SUM(v_order_totals.total_cents), 0) AS gasto_centimos
FROM customers
LEFT JOIN v_order_totals ON v_order_totals.customer_id = customers.id
GROUP BY customers.id, customers.name
ORDER BY gasto_centimos DESC;

SELECT
  products.name AS producto,
  SUM(sales_order_items.quantity) AS unidades_vendidas,
  SUM(sales_order_items.quantity * sales_order_items.unit_price_cents) AS ventas_centimos
FROM sales_order_items
JOIN products ON products.id = sales_order_items.product_id
JOIN sales_orders ON sales_orders.id = sales_order_items.order_id
WHERE sales_orders.status <> 'cancelled'
GROUP BY products.id, products.name
ORDER BY unidades_vendidas DESC, products.name
LIMIT 5;

SELECT *
FROM v_stock_alerts
ORDER BY stock, name;

SELECT *
FROM v_daily_sales
ORDER BY sale_date;

EXPLAIN QUERY PLAN
SELECT id, sku, name, price_cents
FROM products
WHERE active = 1 AND category_id = 1
ORDER BY name COLLATE NOCASE
LIMIT 12;
