BEGIN IMMEDIATE;

INSERT OR IGNORE INTO categories (id, slug, name, description) VALUES
  (1, 'despensa', 'Despensa', 'Productos para cocinar y compartir.'),
  (2, 'hogar', 'Hogar', 'Objetos útiles para la casa.'),
  (3, 'cuidado', 'Cuidado personal', 'Artículos de higiene y cuidado.'),
  (4, 'papeleria', 'Papelería', 'Material para estudiar y organizarse.');

INSERT OR IGNORE INTO suppliers (id, name, email) VALUES
  (1, 'Huerta del Río', 'pedidos@huertadelrio.example.test'),
  (2, 'Taller Norte', 'ventas@tallernorte.example.test'),
  (3, 'Cooperativa Sol', 'contacto@cooperativasol.example.test');

INSERT OR IGNORE INTO payment_methods (id, code, name) VALUES
  (1, 'pickup', 'Pagar en tienda al recoger'),
  (2, 'cash', 'Efectivo al recibir'),
  (3, 'card-demo', 'Tarjeta bancaria (simulación)');

UPDATE payment_methods
SET active = 0
WHERE code = 'cash';

UPDATE payment_methods
SET name = 'Pagar en tienda al recoger'
WHERE code = 'pickup';

UPDATE payment_methods
SET name = 'Tarjeta bancaria (simulación)', active = 1
WHERE code = 'card-demo';

INSERT OR IGNORE INTO products (id, sku, category_id, name, description, price_cents, stock, reorder_level) VALUES
  (1, 'DES-001', 1, 'Aceite de oliva suave', 'Botella de 500 ml, prensado en frío.', 890, 28, 6),
  (2, 'DES-002', 1, 'Miel de romero', 'Tarro de 350 g de producción ficticia.', 740, 16, 5),
  (3, 'DES-003', 1, 'Lentejas pardinas', 'Paquete de 1 kg.', 325, 4, 8),
  (4, 'DES-004', 1, 'Café molido', 'Tueste medio, paquete de 250 g.', 695, 19, 5),
  (5, 'HOG-001', 2, 'Bolsa de algodón', 'Bolsa reutilizable con asas reforzadas.', 590, 21, 6),
  (6, 'HOG-002', 2, 'Jabón lavavajillas', 'Formato concentrado de 500 ml.', 410, 12, 4),
  (7, 'CUI-001', 3, 'Champú sólido', 'Barra de 80 g para uso diario.', 825, 14, 4),
  (8, 'CUI-002', 3, 'Crema de manos', 'Tubo de 75 ml.', 560, 9, 4),
  (9, 'PAP-001', 4, 'Cuaderno cuadriculado', 'Tapa blanda, 120 páginas.', 375, 31, 8),
  (10, 'PAP-002', 4, 'Bolígrafo azul', 'Trazo de 0,7 mm.', 125, 44, 12),
  (11, 'PAP-003', 4, 'Agenda semanal', 'Planificación sin fechas para todo el año.', 980, 7, 5),
  (12, 'HOG-003', 2, 'Paño de cocina', 'Tejido de algodón, pack de dos.', 680, 10, 4);

INSERT OR IGNORE INTO product_suppliers (product_id, supplier_id, supplier_sku, unit_cost_cents) VALUES
  (1, 1, 'HR-ACE-500', 520),
  (2, 1, 'HR-MIE-350', 430),
  (3, 1, 'HR-LEN-1000', 170),
  (4, 3, 'CS-CAF-250', 410),
  (5, 2, 'TN-BOL-ALG', 320),
  (6, 2, 'TN-LAV-500', 210),
  (7, 3, 'CS-CHS-080', 500),
  (8, 3, 'CS-CRE-075', 330),
  (9, 2, 'TN-CUA-120', 190),
  (10, 2, 'TN-BOL-AZ', 60),
  (11, 2, 'TN-AGE-SEM', 590),
  (12, 2, 'TN-PAN-002', 390);

INSERT OR IGNORE INTO customers (id, name, email) VALUES
  (1, 'Alicia Moreno', 'alicia@example.test'),
  (2, 'Diego Martín', 'diego@example.test'),
  (3, 'Sara Vidal', 'sara@example.test');

INSERT OR IGNORE INTO sales_orders (id, order_number, customer_id, payment_method_id, status, payment_status, created_at) VALUES
  (1, 'MB-DEMO-1001', 1, 1, 'completed', 'paid', '2026-09-08T10:15:00Z'),
  (2, 'MB-DEMO-1002', 2, 2, 'completed', 'paid', '2026-09-09T14:30:00Z'),
  (3, 'MB-DEMO-1003', 1, 1, 'pending', 'pending', '2026-09-10T09:20:00Z');

INSERT OR IGNORE INTO sales_order_items (order_id, product_id, quantity, unit_price_cents) VALUES
  (1, 1, 2, 890),
  (1, 4, 1, 695),
  (2, 5, 1, 590),
  (2, 9, 2, 375),
  (3, 2, 1, 740),
  (3, 10, 3, 125);

COMMIT;
