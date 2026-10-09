const http = require("node:http");
const fs = require("node:fs");
const path = require("node:path");
const { AppError } = require("./errors");
const {
  createStore,
  getCategories,
  getPaymentMethods,
  listProducts,
  getProduct,
  getSummary,
  getOrder,
  createOrder
} = require("./store");

const publicDirectory = path.join(__dirname, "public");
const staticFiles = new Map([
  ["/", { filename: "index.html", type: "text/html; charset=utf-8" }],
  ["/styles.css", { filename: "styles.css", type: "text/css; charset=utf-8" }],
  ["/app.js", { filename: "app.js", type: "text/javascript; charset=utf-8" }]
]);

function sendJson(response, status, body) {
  response.writeHead(status, {
    "Content-Type": "application/json; charset=utf-8",
    "Cache-Control": "no-store"
  });
  response.end(JSON.stringify(body));
}

function addSecurityHeaders(response) {
  response.setHeader("X-Content-Type-Options", "nosniff");
  response.setHeader("Referrer-Policy", "no-referrer");
  response.setHeader("X-Frame-Options", "DENY");
  response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:; connect-src 'self'; base-uri 'self'; form-action 'self'; frame-ancestors 'none'");
}

async function readJson(request) {
  const chunks = [];
  let size = 0;

  for await (const chunk of request) {
    size += chunk.length;

    if (size > 16384) {
      throw new AppError(413, "El contenido del pedido es demasiado grande.", "payload_too_large");
    }

    chunks.push(chunk);
  }

  if (size === 0) {
    throw new AppError(400, "Falta el contenido del pedido.", "empty_body");
  }

  try {
    return JSON.parse(Buffer.concat(chunks).toString("utf8"));
  } catch {
    throw new AppError(400, "El contenido no es JSON válido.", "invalid_json");
  }
}

function parsePositiveInteger(value, label, maximum = Number.MAX_SAFE_INTEGER) {
  if (!/^\d+$/.test(value || "")) {
    throw new AppError(400, `${label} debe ser un número entero positivo.`, "invalid_query");
  }

  const number = Number(value);

  if (!Number.isSafeInteger(number) || number < 1 || number > maximum) {
    throw new AppError(400, `${label} está fuera del intervalo permitido.`, "invalid_query");
  }

  return number;
}

function serveStatic(response, pathname) {
  const file = staticFiles.get(pathname);

  if (!file) {
    return false;
  }

  const filename = path.join(publicDirectory, file.filename);
  response.writeHead(200, {
    "Content-Type": file.type,
    "Cache-Control": "no-cache"
  });
  fs.createReadStream(filename).pipe(response);
  return true;
}

function createServer({ databasePath } = {}) {
  const database = createStore({ filename: databasePath || path.join(__dirname, "data", "mercado.sqlite") });

  const server = http.createServer(async (request, response) => {
    addSecurityHeaders(response);

    try {
      const url = new URL(request.url || "/", "http://127.0.0.1");
      let pathname;

      try {
        pathname = decodeURIComponent(url.pathname);
      } catch {
        throw new AppError(400, "La ruta no tiene un formato válido.", "invalid_path");
      }

      if (request.method === "GET" && pathname.startsWith("/api/")) {
        if (pathname === "/api/health") {
          database.prepare("SELECT 1 AS available").get();
          sendJson(response, 200, { status: "ok" });
          return;
        }

        if (pathname === "/api/categories") {
          sendJson(response, 200, { items: getCategories(database) });
          return;
        }

        if (pathname === "/api/payment-methods") {
          sendJson(response, 200, { items: getPaymentMethods(database) });
          return;
        }

        if (pathname === "/api/products") {
          const page = parsePositiveInteger(url.searchParams.get("page") || "1", "La página", 10000);
          const limit = parsePositiveInteger(url.searchParams.get("limit") || "12", "El límite", 24);
          const categoryValue = url.searchParams.get("category");
          const categoryId = categoryValue ? parsePositiveInteger(categoryValue, "La categoría") : null;
          const search = (url.searchParams.get("q") || "").trim();
          const sort = url.searchParams.get("sort") || "name";
          const validSorts = new Set(["name", "price-asc", "price-desc"]);

          if (search.length > 80) {
            throw new AppError(400, "La búsqueda no puede superar los 80 caracteres.", "invalid_query");
          }

          if (!validSorts.has(sort)) {
            throw new AppError(400, "El criterio de ordenación no es válido.", "invalid_query");
          }

          sendJson(response, 200, listProducts(database, { categoryId, search, sort, page, limit }));
          return;
        }

        if (pathname === "/api/reports/summary") {
          sendJson(response, 200, getSummary(database));
          return;
        }

        const productMatch = pathname.match(/^\/api\/products\/([1-9]\d*)$/);

        if (productMatch) {
          const product = getProduct(database, Number(productMatch[1]));

          if (!product) {
            throw new AppError(404, "No se encontró el producto.", "not_found");
          }

          sendJson(response, 200, product);
          return;
        }

        const orderMatch = pathname.match(/^\/api\/orders\/(MB-(?:[A-F0-9]{12}|DEMO-\d{4}))$/i);

        if (orderMatch) {
          const order = getOrder(database, orderMatch[1].toUpperCase());

          if (!order) {
            throw new AppError(404, "No se encontró el pedido.", "not_found");
          }

          sendJson(response, 200, order);
          return;
        }

        throw new AppError(404, "No se encontró la ruta.", "not_found");
      }

      if (request.method === "POST" && pathname === "/api/orders") {
        if (!String(request.headers["content-type"] || "").toLowerCase().startsWith("application/json")) {
          throw new AppError(415, "El pedido debe enviarse como JSON.", "unsupported_media_type");
        }

        const order = createOrder(database, await readJson(request));
        sendJson(response, 201, order);
        return;
      }

      if (request.method === "GET" && serveStatic(response, pathname)) {
        return;
      }

      throw new AppError(404, "No se encontró la ruta.", "not_found");
    } catch (error) {
      if (response.headersSent) {
        response.destroy();
        return;
      }

      const status = error instanceof AppError ? error.status : 500;
      const code = error instanceof AppError ? error.code : "internal_error";
      const message = error instanceof AppError ? error.message : "No se pudo completar la operación.";
      sendJson(response, status, { error: { code, message } });
    }
  });

  server.database = database;
  return server;
}

if (require.main === module) {
  const server = createServer();
  const port = Number(process.env.PORT || 3000);

  if (!Number.isInteger(port) || port < 1 || port > 65535) {
    process.stderr.write("El puerto indicado no es válido.\n");
    server.database.close();
    process.exitCode = 1;
  } else {
    server.listen(port, "127.0.0.1", () => {
      process.stdout.write(`La Despensa está disponible en http://127.0.0.1:${port}\n`);
    });

    const stop = () => {
      server.close(() => {
        server.database.close();
        process.exit(0);
      });
    };

    process.on("SIGINT", stop);
    process.on("SIGTERM", stop);
  }
}

module.exports = { createServer };
