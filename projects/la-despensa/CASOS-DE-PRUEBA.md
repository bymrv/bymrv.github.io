# Pruebas de La Despensa

## Ejecutar

Requiere Node.js 24.15.0 o posterior. Desde la carpeta de La Despensa:

```sh
node --test
```

Las pruebas crean bases de datos SQLite en memoria. Las que comprueban HTTP levantan su propio servidor en `127.0.0.1` con un puerto libre. No requieren arrancar `server.js` por separado ni modifican los pedidos de una demo en uso.

## Casos y resultados

| ID | Acción | Comprobación | Resultado local |
| --- | --- | --- | --- |
| LD-01 | Ejecutar las consultas de `database/queries.sql` | Las ocho consultas devuelven filas con los datos de ejemplo | Superada |
| LD-02 | Consultar estado y categorías | Respuestas 200, estado `ok` y cuatro categorías | Superada |
| LD-03 | Filtrar y ordenar productos; buscar una cadena con sintaxis SQL | Filtro y orden correctos; la cadena no altera la consulta | Superada |
| LD-04 | Solicitar el catálogo con `page=0` | Respuesta 400 con código `invalid_query` | Superada |
| LD-05 | Enviar un pedido válido con dos líneas | Respuesta 201, total 2475 céntimos, líneas guardadas y descuento de existencias | Superada |
| LD-06 | Enviar un pedido cuya segunda línea supera las existencias | Respuesta 409; se revierten pedido, cliente y cambios de stock | Superada |
| LD-07 | Insertar directamente una línea que supera el stock | SQLite rechaza la inserción | Superada |
| LD-08 | Solicitar la web, una ruta no pública, una URL mal formada y un pedido de ejemplo | Cabecera CSP presente; respuestas 404, 400 y 200 según la petición | Superada |
| LD-09 | Consultar el resumen de datos de ejemplo | 12 productos, 4 categorías, 3 pedidos y 4930 céntimos de ventas | Superada |

## Registro

- Fecha: 9 de octubre de 2026.
- Entorno: Windows y Node.js 24.19.0.
- Resultado: 9 pruebas ejecutadas y superadas; 0 fallos; 0 omitidas.
- Código: [queries.test.js](test/queries.test.js) y [store.test.js](test/store.test.js).

El resultado corresponde a esta ejecución local. El flujo de GitHub Actions ya incluido permite repetir las pruebas al subir cambios en el proyecto; no se presenta aquí como una ejecución remota ya completada.

## Alcance

Estas pruebas cubren consultas, reglas de datos y llamadas HTTP. La interfaz visual, la accesibilidad y los distintos navegadores necesitan comprobaciones adicionales. Una búsqueda con una cadena SQL no equivale a una auditoría completa de seguridad.

## English

Run `node --test` with Node.js 24.15.0 or later. Tests use in-memory SQLite databases and start their own local HTTP servers on available ports.

All nine tests passed locally on October 9, 2026 using Node.js 24.19.0 on Windows. The suite checks SQL queries, health and categories, product filtering, invalid query parameters, valid orders, transaction rollback, stock constraints, selected HTTP routes and the summary report.

This is a record of a local run. The tests do not establish complete UI, accessibility, browser compatibility or security coverage. The source files linked above contain the exact assertions.
