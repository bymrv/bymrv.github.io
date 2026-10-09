USE tienda;

INSERT INTO productos (nombre, precio, cantidad)
SELECT 'Teclado mecánico', 69.90, 12
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Teclado mecánico');

INSERT INTO productos (nombre, precio, cantidad)
SELECT 'Ratón inalámbrico', 24.50, 30
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Ratón inalámbrico');

INSERT INTO productos (nombre, precio, cantidad)
SELECT 'Monitor 24 pulgadas', 149.99, 8
WHERE NOT EXISTS (SELECT 1 FROM productos WHERE nombre = 'Monitor 24 pulgadas');
