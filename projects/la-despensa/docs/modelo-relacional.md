# Modelo relacional

## Entidades

| Tabla | Responsabilidad | Reglas principales |
|---|---|---|
| `categories` | Clasifica los productos | Slug y nombre únicos |
| `products` | Catálogo e inventario | SKU único, precio entero en céntimos, stock no negativo |
| `suppliers` | Proveedores ficticios | Nombre y correo únicos |
| `product_suppliers` | Relación producto-proveedor | Clave primaria compuesta y coste de compra |
| `customers` | Datos mínimos de quien hace un pedido | Correo único; no almacena contraseñas |
| `payment_methods` | Pago en tienda y opción de tarjeta de demostración | Código y nombre únicos; no procesa pagos |
| `sales_orders` | Cabecera y estado del pedido | Número único, cliente y método existentes |
| `sales_order_items` | Productos y precio acordado en el pedido | Un producto por pedido, cantidad positiva |

## Relaciones

- Una categoría puede tener muchos productos; cada producto pertenece a una categoría.
- Productos y proveedores tienen una relación de muchos a muchos resuelta mediante `product_suppliers`.
- Un cliente puede realizar varios pedidos.
- Cada pedido tiene un método de pago de demostración y una o varias líneas.
- Cada línea conserva el precio del momento de la compra, aunque el precio de catálogo cambie más adelante.

## Integridad y consultas

Las claves foráneas impiden guardar referencias inexistentes. Las restricciones `CHECK` validan importes, cantidades, estados y disponibilidad. Los índices acompañan las búsquedas más frecuentes: catálogo por categoría y nombre, precio, pedidos por cliente o estado, y productos por proveedor.

`v_order_totals` calcula unidades e importe sin duplicar esos valores en la cabecera. `v_stock_alerts` reúne productos que han llegado a su nivel de reposición. `v_daily_sales` resume pedidos por día. `trg_order_item_stock_guard` rechaza líneas sin producto activo o con unidades insuficientes; `trg_order_item_stock_update` descuenta el inventario cuando se inserta una línea.

La API agrupa cada alta de pedido en una transacción. Si falla una línea, el pedido, el cliente nuevo y cualquier cambio de stock de esa operación se revierten juntos.
