const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const { test } = require("node:test");
const { createStore } = require("../store");

test("las consultas de análisis se ejecutan con los datos de ejemplo", () => {
  const database = createStore({ filename: ":memory:" });
  const queryPath = path.join(__dirname, "..", "database", "queries.sql");
  const queries = fs.readFileSync(queryPath, "utf8")
    .split(";")
    .map((query) => query.trim())
    .filter(Boolean);

  try {
    assert.equal(queries.length, 8);

    for (const query of queries) {
      assert.ok(database.prepare(query).all().length > 0);
    }
  } finally {
    database.close();
  }
});
