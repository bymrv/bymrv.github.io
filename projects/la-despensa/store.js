const fs = require("node:fs");
const path = require("node:path");
const { randomUUID } = require("node:crypto");
const { DatabaseSync } = require("node:sqlite");
const { AppError } = require("./errors");

const schemaPath = path.join(__dirname, "database", "schema.sql");
const seedPath = path.join(__dirname, "database", "seed.sql");

function createStore({ filename = path.join(__dirname, "data", "mercado.sqlite"), seed = true } = {}) {
  if (filename !== ":memory:") {
    fs.mkdirSync(path.dirname(filename), { recursive: true });
  }

  const database = new DatabaseSync(filename);
  database.exec("PRAGMA foreign_keys = ON; PRAGMA busy_timeout = 5000;");
  database.exec(fs.readFileSync(schemaPath, "utf8"));

  if (seed) {
    database.exec(fs.readFileSync(seedPath, "utf8"));
  }

  return database;
}

function getCategories(database) {
  return database.prepare(
    "SELECT id, slug, name, description FROM categories ORDER BY name COLLATE NOCASE"
  ).all();
}

function getPaymentMethods(database) {
  return database.prepare(
    "SELECT code, name FROM payment_methods WHERE active = 1 ORDER BY id"
  ).all();
}

function listProducts(database, { categoryId = null, search = "", sort = "name", page = 1, limit = 12 } = {}) {
  const filters = ["products.active = 1"];
  const values = [];

  if (categoryId !== null) {
    filters.push("products.category_id = ?");
    values.push(categoryId);
  }

  if (search) {
    filters.push("(products.name LIKE ? COLLATE NOCASE OR products.description LIKE ? COLLATE NOCASE)");
    values.push(`%${search}%`, `%${search}%`);
  }

  const where = filters.join(" AND ");
  const sortExpressions = {
    name: "products.name COLLATE NOCASE ASC",
    "price-asc": "products.price_cents ASC, products.name COLLATE NOCASE ASC",
    "price-desc": "products.price_cents DESC, products.name COLLATE NOCASE ASC"
  };
  const sortExpression = sortExpressions[sort] || sortExpressions.name;
  const total = database.prepare(`SELECT COUNT(*) AS total FROM products WHERE ${where}`).get(...values).total;
  const offset = (page - 1) * limit;
  const items = database.prepare(`
    SELECT
      products.id,
      products.sku,
      products.name,
      products.description,
      products.price_cents,
      products.stock,
      categories.id AS category_id,
      categories.name AS category_name,
      categories.slug AS category_slug
    FROM products
    JOIN categories ON categories.id = products.category_id
    WHERE ${where}
    ORDER BY ${sortExpression}
    LIMIT ? OFFSET ?
  `).all(...values, limit, offset);

  return {
    items,
    pagination: {
      page,
      limit,
      total,
      totalPages: Math.ceil(total / limit)
    }
  };
}

function getProduct(database, id) {
  return database.prepare(`
    SELECT
      products.id,
      products.sku,
      products.name,
      products.description,
      products.price_cents,
      products.stock,
      categories.id AS category_id,
      categories.name AS category_name,
      categories.slug AS category_slug
    FROM products
    JOIN categories ON categories.id = products.category_id
    WHERE products.id = ? AND products.active = 1
  `).get(id) || null;
}

function getSummary(database) {
  const catalogue = database.prepare(`
    SELECT
      COUNT(*) AS product_count,
      COUNT(DISTINCT category_id) AS category_count,
      COALESCE(SUM(stock), 0) AS units_available
    FROM products
    WHERE active = 1
  `).get();
  const alerts = database.prepare("SELECT COUNT(*) AS low_stock_count FROM v_stock_alerts").get();
  const sales = database.prepare(`
    SELECT
      COUNT(*) AS order_count,
      COALESCE(SUM(total_cents), 0) AS sales_cents
    FROM v_order_totals
    WHERE status <> 'cancelled'
  `).get();

  return { ...catalogue, ...alerts, ...sales };
}

function getOrder(database, orderNumber) {
  const order = database.prepare(`
    SELECT
      v_order_totals.order_number,
      v_order_totals.status,
      v_order_totals.payment_status,
      v_order_totals.created_at,
      v_order_totals.total_cents,
      v_order_totals.item_count,
      customers.name AS customer_name,
      payment_methods.name AS payment_method
    FROM v_order_totals
    JOIN customers ON customers.id = v_order_totals.customer_id
    JOIN payment_methods ON payment_methods.id = v_order_totals.payment_method_id
    WHERE v_order_totals.order_number = ?
  `).get(orderNumber);

  if (!order) {
    return null;
  }

  const items = database.prepare(`
    SELECT
      products.sku,
      products.name,
      sales_order_items.quantity,
      sales_order_items.unit_price_cents,
      sales_order_items.quantity * sales_order_items.unit_price_cents AS line_total_cents
    FROM sales_order_items
    JOIN products ON products.id = sales_order_items.product_id
    JOIN sales_orders ON sales_orders.id = sales_order_items.order_id
    WHERE sales_orders.order_number = ?
    ORDER BY products.name COLLATE NOCASE
  `).all(orderNumber);

  return { ...order, items };
}

function normalizeOrder(input) {
  if (!input || typeof input !== "object" || Array.isArray(input)) {
    throw new AppError(400, "El pedido no tiene un formato válido.", "invalid_order");
  }

  const customer = input.customer;
  const name = typeof customer?.name === "string" ? customer.name.trim() : "";
  const email = typeof customer?.email === "string" ? customer.email.trim().toLowerCase() : "";
  const validName = /^[\p{L}]+(?:[ '\u2019-][\p{L}]+)*$/u.test(name);

  if (name.length < 2 || name.length > 100 || !validName) {
    throw new AppError(400, "Escribe un nombre válido con letras, espacios, guiones o apóstrofes.", "invalid_customer");
  }

  if (email.length > 150 || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    throw new AppError(400, "Escribe un correo electrónico válido.", "invalid_customer");
  }

  if (!Array.isArray(input.items) || input.items.length < 1 || input.items.length > 30) {
    throw new AppError(400, "El pedido debe incluir entre 1 y 30 líneas.", "invalid_items");
  }

  const merged = new Map();

  for (const item of input.items) {
    const productId = Number(item?.productId);
    const quantity = Number(item?.quantity);

    if (!Number.isSafeInteger(productId) || productId < 1 || !Number.isSafeInteger(quantity) || quantity < 1 || quantity > 99) {
      throw new AppError(400, "Revisa los productos y las cantidades del pedido.", "invalid_items");
    }

    const nextQuantity = (merged.get(productId) || 0) + quantity;

    if (nextQuantity > 99) {
      throw new AppError(400, "La cantidad máxima por producto es 99.", "invalid_items");
    }

    merged.set(productId, nextQuantity);
  }

  const paymentMethod = typeof input.paymentMethod === "string" ? input.paymentMethod : "pickup";

  return {
    customer: { name, email },
    paymentMethod,
    items: [...merged].map(([productId, quantity]) => ({ productId, quantity }))
  };
}

function createOrder(database, input) {
  const orderInput = normalizeOrder(input);
  const paymentMethod = database.prepare(
    "SELECT id FROM payment_methods WHERE code = ? AND active = 1"
  ).get(orderInput.paymentMethod);

  if (!paymentMethod) {
    throw new AppError(400, "El método de pago no está disponible.", "invalid_payment_method");
  }

  database.exec("BEGIN IMMEDIATE");

  try {
    database.prepare(`
      INSERT INTO customers (name, email)
      VALUES (?, ?)
      ON CONFLICT(email) DO UPDATE SET name = excluded.name
    `).run(orderInput.customer.name, orderInput.customer.email);

    const customer = database.prepare("SELECT id FROM customers WHERE email = ?").get(orderInput.customer.email);
    const orderNumber = `MB-${randomUUID().replaceAll("-", "").slice(0, 12).toUpperCase()}`;
    const result = database.prepare(`
      INSERT INTO sales_orders (order_number, customer_id, payment_method_id)
      VALUES (?, ?, ?)
    `).run(orderNumber, customer.id, paymentMethod.id);
    const insertItem = database.prepare(`
      INSERT INTO sales_order_items (order_id, product_id, quantity, unit_price_cents)
      VALUES (?, ?, ?, ?)
    `);
    const findProduct = database.prepare(`
      SELECT id, price_cents, stock, active
      FROM products
      WHERE id = ?
    `);

    for (const item of orderInput.items) {
      const product = findProduct.get(item.productId);

      if (!product || product.active !== 1) {
        throw new AppError(409, "Uno de los productos ya no está disponible.", "product_unavailable");
      }

      if (product.stock < item.quantity) {
        throw new AppError(409, `No quedan unidades suficientes de ${getProduct(database, item.productId).name}.`, "insufficient_stock");
      }

      insertItem.run(result.lastInsertRowid, item.productId, item.quantity, product.price_cents);
    }

    database.exec("COMMIT");
    return getOrder(database, orderNumber);
  } catch (error) {
    if (database.isTransaction) {
      database.exec("ROLLBACK");
    }

    if (error instanceof AppError) {
      throw error;
    }

    if (String(error.message).includes("No hay existencias suficientes")) {
      throw new AppError(409, "No quedan unidades suficientes para completar el pedido.", "insufficient_stock");
    }

    throw error;
  }
}

module.exports = {
  createStore,
  getCategories,
  getPaymentMethods,
  listProducts,
  getProduct,
  getSummary,
  getOrder,
  createOrder
};
