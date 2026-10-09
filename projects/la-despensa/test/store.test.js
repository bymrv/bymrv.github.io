const assert = require("node:assert/strict");
const { afterEach, beforeEach, test } = require("node:test");
const { createServer } = require("../server");

let server;
let baseUrl;

beforeEach(async () => {
  server = createServer({ databasePath: ":memory:" });
  await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
  const address = server.address();
  baseUrl = `http://127.0.0.1:${address.port}`;
});

afterEach(async () => {
  server.closeAllConnections();
  await new Promise((resolve, reject) => server.close((error) => error ? reject(error) : resolve()));
  server.database.close();
});

async function getJson(route, options) {
  const response = await fetch(`${baseUrl}${route}`, options);
  return { response, body: await response.json() };
}

test("la API comprueba la conexión y entrega categorías", async () => {
  const health = await getJson("/api/health");
  const categories = await getJson("/api/categories");

  assert.equal(health.response.status, 200);
  assert.deepEqual(health.body, { status: "ok" });
  assert.equal(categories.body.items.length, 4);
});

test("el catálogo filtra con parámetros y consultas seguras", async () => {
  const filtered = await getJson("/api/products?category=1&sort=price-asc&limit=3");
  const injection = await getJson("/api/products?q=%25%27%20OR%201%3D1%20--");

  assert.equal(filtered.response.status, 200);
  assert.equal(filtered.body.pagination.total, 4);
  assert.equal(filtered.body.items.length, 3);
  assert.ok(filtered.body.items[0].price_cents <= filtered.body.items[1].price_cents);
  assert.equal(injection.body.pagination.total, 0);
});

test("el catálogo rechaza parámetros fuera de rango", async () => {
  const response = await getJson("/api/products?page=0");

  assert.equal(response.response.status, 400);
  assert.equal(response.body.error.code, "invalid_query");
});

test("un pedido guarda sus líneas y descuenta existencias", async () => {
  const beforeStock = server.database.prepare("SELECT stock FROM products WHERE id = 1").get().stock;
  const response = await getJson("/api/orders", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      customer: { name: "Nuria Campos", email: "nuria@example.test" },
      paymentMethod: "pickup",
      items: [{ productId: 1, quantity: 2 }, { productId: 4, quantity: 1 }]
    })
  });
  const afterStock = server.database.prepare("SELECT stock FROM products WHERE id = 1").get().stock;
  const saved = await getJson(`/api/orders/${response.body.order_number}`);

  assert.equal(response.response.status, 201);
  assert.match(response.body.order_number, /^MB-[A-F0-9]{12}$/);
  assert.equal(response.body.total_cents, 2475);
  assert.equal(afterStock, beforeStock - 2);
  assert.equal(saved.body.items.length, 2);
  assert.equal(Object.hasOwn(saved.body, "customer_email"), false);
});

test("si una línea falla, se revierte el pedido y el stock", async () => {
  const beforeStock = server.database.prepare("SELECT stock FROM products WHERE id = 1").get().stock;
  const beforeOrders = server.database.prepare("SELECT COUNT(*) AS total FROM sales_orders").get().total;
  const response = await getJson("/api/orders", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      customer: { name: "Inés Roca", email: "ines@example.test" },
      paymentMethod: "pickup",
      items: [{ productId: 1, quantity: 1 }, { productId: 3, quantity: 99 }]
    })
  });
  const afterStock = server.database.prepare("SELECT stock FROM products WHERE id = 1").get().stock;
  const afterOrders = server.database.prepare("SELECT COUNT(*) AS total FROM sales_orders").get().total;

  assert.equal(response.response.status, 409);
  assert.equal(response.body.error.code, "insufficient_stock");
  assert.equal(afterStock, beforeStock);
  assert.equal(afterOrders, beforeOrders);
  assert.equal(server.database.prepare("SELECT COUNT(*) AS total FROM customers WHERE email = 'ines@example.test'").get().total, 0);
});

test("el esquema rechaza una línea que supera el stock", () => {
  const order = server.database.prepare("SELECT id FROM sales_orders WHERE order_number = 'MB-DEMO-1001'").get();

  assert.throws(
    () => server.database.prepare("INSERT INTO sales_order_items (order_id, product_id, quantity, unit_price_cents) VALUES (?, 3, 99, 325)").run(order.id),
    /No hay existencias suficientes/
  );
});

test("la web sirve cabeceras de seguridad y no acepta rutas arbitrarias", async () => {
  const page = await fetch(baseUrl);
  const missing = await getJson("/server.js");
  const malformed = await getJson("/api/%E0%A4%A");
  const sampleOrder = await getJson("/api/orders/MB-DEMO-1001");

  assert.equal(page.status, 200);
  assert.match(page.headers.get("content-security-policy"), /default-src 'self'/);
  assert.equal(missing.response.status, 404);
  assert.equal(malformed.response.status, 400);
  assert.equal(sampleOrder.response.status, 200);
  assert.equal(sampleOrder.body.order_number, "MB-DEMO-1001");
});

test("el resumen coincide con los datos de ejemplo", async () => {
  const response = await getJson("/api/reports/summary");

  assert.equal(response.body.product_count, 12);
  assert.equal(response.body.category_count, 4);
  assert.equal(response.body.order_count, 3);
  assert.equal(response.body.sales_cents, 4930);
});
