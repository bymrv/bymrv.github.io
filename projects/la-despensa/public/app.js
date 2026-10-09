const money = new Intl.NumberFormat("es-ES", { style: "currency", currency: "EUR" });
const cart = new Map();
let currentPage = 1;
let totalPages = 1;

const productGrid = document.querySelector("#product-grid");
const catalogMessage = document.querySelector("#catalog-message");
const categoryFilter = document.querySelector("#category-filter");
const sortFilter = document.querySelector("#sort-filter");
const searchInput = document.querySelector("#search-input");
const basketItems = document.querySelector("#basket-items");
const basketEmpty = document.querySelector("#basket-empty");
const basketTotal = document.querySelector("#basket-total");
const basketCount = document.querySelector("#basket-count");
const basketMessage = document.querySelector("#basket-message");
const cartCount = document.querySelector("#cart-count");
const checkoutButton = document.querySelector("#checkout-button");
const checkoutMessage = document.querySelector("#checkout-message");
const paymentMethodSelect = document.querySelector("#payment-method");
const paymentNote = document.querySelector("#payment-note");
const pagination = document.querySelector("#pagination");

async function requestJson(url, options = {}) {
  const response = await fetch(url, options);
  const body = await response.json();

  if (!response.ok) {
    throw new Error(body.error?.message || "No se pudo completar la operación.");
  }

  return body;
}

function makeElement(tag, className, text) {
  const element = document.createElement(tag);

  if (className) {
    element.className = className;
  }

  if (text !== undefined) {
    element.textContent = text;
  }

  return element;
}

function productCard(product) {
  const article = makeElement("article", "product-card");
  const art = makeElement("div", "product-art");
  art.dataset.category = product.category_slug;
  art.setAttribute("aria-hidden", "true");

  const mark = makeElement("span", "product-mark", product.category_name.slice(0, 1));
  art.append(mark);

  const category = makeElement("p", "product-type", product.category_name);
  const title = makeElement("h3", "", product.name);
  const description = makeElement("p", "product-description", product.description);
  const footer = makeElement("div", "product-footer");
  const price = makeElement("strong", "product-price", money.format(product.price_cents / 100));
  const addButton = makeElement("button", "add-button", "Añadir");
  addButton.type = "button";
  addButton.disabled = product.stock < 1;
  addButton.setAttribute("aria-label", `Añadir ${product.name} a la cesta`);
  addButton.addEventListener("click", () => addToCart(product));
  footer.append(price, addButton);

  const stockText = product.stock === 0 ? "Agotado" : `${product.stock} ${product.stock === 1 ? "unidad" : "unidades"} disponibles`;
  const stock = makeElement("p", product.stock > 0 && product.stock <= 5 ? "stock-label low-stock" : "stock-label", stockText);
  article.append(art, category, title, description, footer, stock);
  return article;
}

async function loadCategories() {
  const result = await requestJson("/api/categories");

  for (const category of result.items) {
    const option = document.createElement("option");
    option.value = String(category.id);
    option.textContent = category.name;
    categoryFilter.append(option);
  }
}

async function loadPaymentMethods() {
  const result = await requestJson("/api/payment-methods");

  for (const method of result.items) {
    const option = document.createElement("option");
    option.value = method.code;
    option.textContent = method.name;
    paymentMethodSelect.append(option);
  }
}

async function loadProducts() {
  catalogMessage.textContent = "Cargando productos…";
  const parameters = new URLSearchParams({
    page: String(currentPage),
    limit: "12",
    sort: sortFilter.value
  });

  if (categoryFilter.value) {
    parameters.set("category", categoryFilter.value);
  }

  if (searchInput.value.trim()) {
    parameters.set("q", searchInput.value.trim());
  }

  const result = await requestJson(`/api/products?${parameters}`);
  productGrid.replaceChildren(...result.items.map(productCard));
  totalPages = Math.max(1, result.pagination.totalPages);
  catalogMessage.textContent = result.pagination.total === 1
    ? "1 producto encontrado"
    : `${result.pagination.total} productos encontrados`;
  pagination.hidden = totalPages < 2;
  document.querySelector("#page-label").textContent = `Página ${currentPage} de ${totalPages}`;
  document.querySelector("#previous-page").disabled = currentPage <= 1;
  document.querySelector("#next-page").disabled = currentPage >= totalPages;
}

async function loadSummary() {
  const summary = await requestJson("/api/reports/summary");
  document.querySelector("#summary-products").textContent = String(summary.product_count);
  document.querySelector("#summary-categories").textContent = String(summary.category_count);
  document.querySelector("#summary-orders").textContent = String(summary.order_count);
  document.querySelector("#summary-low-stock").textContent = String(summary.low_stock_count);
}

function addToCart(product) {
  const current = cart.get(product.id);
  const quantity = (current?.quantity || 0) + 1;

  if (quantity > product.stock) {
    basketMessage.textContent = `Ya has añadido todas las unidades disponibles de ${product.name}.`;
    return;
  }

  cart.set(product.id, { product, quantity });
  checkoutMessage.textContent = "";
  checkoutMessage.classList.remove("error");
  basketMessage.textContent = `${product.name} añadido a la cesta.`;
  renderCart();
}

function updateQuantity(productId, change) {
  const current = cart.get(productId);

  if (!current) {
    return;
  }

  const quantity = current.quantity + change;

  if (quantity < 1) {
    cart.delete(productId);
    basketMessage.textContent = `${current.product.name} eliminado de la cesta.`;
  } else if (quantity <= current.product.stock) {
    cart.set(productId, { ...current, quantity });
    basketMessage.textContent = `Cantidad de ${current.product.name}: ${quantity}.`;
  }

  renderCart();
}

function renderCart() {
  basketItems.replaceChildren();
  const entries = [...cart.values()];
  const itemCount = entries.reduce((total, entry) => total + entry.quantity, 0);
  const subtotal = entries.reduce((total, entry) => total + entry.quantity * entry.product.price_cents, 0);
  basketCount.textContent = String(itemCount);
  basketCount.setAttribute("aria-label", `${itemCount} ${itemCount === 1 ? "unidad" : "unidades"} en la cesta`);
  cartCount.textContent = String(itemCount);
  basketTotal.textContent = money.format(subtotal / 100);
  basketEmpty.hidden = entries.length > 0;
  checkoutButton.disabled = entries.length === 0;

  for (const { product, quantity } of entries) {
    const item = makeElement("li", "basket-item");
    const line = makeElement("div", "basket-item-line");
    const details = makeElement("div", "basket-item-details");
    const name = makeElement("span", "basket-item-name", product.name);
    const unitPrice = makeElement("span", "basket-unit-price", `${money.format(product.price_cents / 100)} por unidad`);
    details.append(name, unitPrice);
    const total = makeElement("strong", "basket-item-total", money.format(quantity * product.price_cents / 100));
    line.append(details, total);
    const actions = makeElement("div", "basket-item-actions");
    const controls = makeElement("div", "quantity-controls");
    const decrease = makeElement("button", "", "−");
    decrease.type = "button";
    decrease.setAttribute("aria-label", `Quitar una unidad de ${product.name}`);
    decrease.addEventListener("click", () => updateQuantity(product.id, -1));
    const count = makeElement("span", "basket-quantity", String(quantity));
    const increase = makeElement("button", "", "+");
    increase.type = "button";
    increase.disabled = quantity >= product.stock;
    increase.setAttribute("aria-label", `Añadir una unidad de ${product.name}`);
    increase.addEventListener("click", () => updateQuantity(product.id, 1));
    controls.append(decrease, count, increase);

    const remove = makeElement("button", "remove-item", "Quitar");
    remove.type = "button";
    remove.setAttribute("aria-label", `Quitar ${product.name} de la cesta`);
    remove.addEventListener("click", () => {
      cart.delete(product.id);
      basketMessage.textContent = `${product.name} eliminado de la cesta.`;
      renderCart();
    });

    actions.append(controls, remove);
    item.append(line, actions);
    basketItems.append(item);
  }
}

function updatePaymentNote() {
  if (paymentMethodSelect.value === "pickup") {
    paymentNote.textContent = "Pagarás en la tienda cuando recojas el pedido.";
  } else if (paymentMethodSelect.value === "card-demo") {
    paymentNote.textContent = "Modo de demostración: no se piden datos de tarjeta ni se realiza ningún cargo.";
  } else {
    paymentNote.textContent = "Elige cómo pagar. La opción de tarjeta solo es una simulación.";
  }
}

async function submitOrder(event) {
  event.preventDefault();

  if (cart.size === 0) {
    return;
  }

  checkoutButton.disabled = true;
  checkoutMessage.textContent = "Guardando el pedido…";
  checkoutMessage.classList.remove("error");

  const form = event.currentTarget;
  const body = {
    customer: {
      name: form.elements.customerName.value,
      email: form.elements.customerEmail.value
    },
    paymentMethod: form.elements.paymentMethod.value,
    items: [...cart.values()].map(({ product, quantity }) => ({ productId: product.id, quantity }))
  };

  try {
    const order = await requestJson("/api/orders", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    });

    cart.clear();
    form.reset();
    basketMessage.textContent = "";
    updatePaymentNote();
    renderCart();
    const paymentMessage = body.paymentMethod === "card-demo"
      ? "Pago con tarjeta simulado; no se ha realizado ningún cargo."
      : "Pagarás en tienda cuando recojas el pedido.";
    checkoutMessage.textContent = `Pedido ${order.order_number} guardado. Total: ${money.format(order.total_cents / 100)}. ${paymentMessage}`;
    await Promise.all([loadProducts(), loadSummary()]);
  } catch (error) {
    checkoutMessage.textContent = error.message;
    checkoutMessage.classList.add("error");
    checkoutButton.disabled = cart.size === 0;
  }
}

document.querySelector("#search-form").addEventListener("submit", async (event) => {
  event.preventDefault();
  currentPage = 1;

  try {
    await loadProducts();
  } catch (error) {
    catalogMessage.textContent = error.message;
    productGrid.replaceChildren();
  }
});

categoryFilter.addEventListener("change", async () => {
  currentPage = 1;

  try {
    await loadProducts();
  } catch (error) {
    catalogMessage.textContent = error.message;
  }
});

sortFilter.addEventListener("change", async () => {
  currentPage = 1;

  try {
    await loadProducts();
  } catch (error) {
    catalogMessage.textContent = error.message;
  }
});

document.querySelector("#previous-page").addEventListener("click", async () => {
  currentPage = Math.max(1, currentPage - 1);

  try {
    await loadProducts();
  } catch (error) {
    catalogMessage.textContent = error.message;
  }
});

document.querySelector("#next-page").addEventListener("click", async () => {
  currentPage = Math.min(totalPages, currentPage + 1);

  try {
    await loadProducts();
  } catch (error) {
    catalogMessage.textContent = error.message;
  }
});

document.querySelector("#checkout-form").addEventListener("submit", submitOrder);
paymentMethodSelect.addEventListener("change", updatePaymentNote);

async function start() {
  try {
    await Promise.all([loadCategories(), loadPaymentMethods(), loadSummary()]);
    await loadProducts();
  } catch (error) {
    catalogMessage.textContent = error.message;
  }
}

start();
