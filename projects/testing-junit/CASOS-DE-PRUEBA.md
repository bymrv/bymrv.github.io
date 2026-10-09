# Casos de prueba: remuneraciones

## Objetivo

Comprobar las dos fórmulas incluidas en la práctica original. Cada prueba prepara su propio objeto con `@BeforeEach` y verifica el resultado con `assertEquals`.

| ID | Pasos y datos | Resultado esperado | Resultado observado |
| --- | --- | --- | --- |
| JU-01 | Crear un autónomo con base 1800. Establecer precio por hora 35 y horas trabajadas 60. Ejecutar `calcularRemuneracion()`. | 3900, con tolerancia 0,1 | Aserción superada |
| JU-02 | Crear una trabajadora por cuenta ajena. Establecer base `2000.0f / 3.0f`. Ejecutar `calcularRemuneracion()`, que aplica el factor 1,18. | 786,67, con tolerancia 0,1 | Aserción superada |

## Registro de ejecución

- Fecha: 9 de octubre de 2026.
- Entorno: Windows, JDK 25, compilación con `release 17`, Maven 3.9.9 y JUnit 5.13.4.
- Orden: `mvn --batch-mode --no-transfer-progress test`.
- Resultado: 2 pruebas ejecutadas; 2 superadas; 0 fallos; 0 errores; 0 omitidas.
- Informes de esa ejecución: [AutonomoTest](reports/AutonomoTest.txt) y [TrabajadorCuentaAjenaTest](reports/TrabajadorCuentaAjenaTest.txt).

Al ejecutar el proyecto de nuevo, los resultados actuales se generan en `target/surefire-reports/`. Los archivos de `reports/` son una copia de la ejecución indicada, no un estado actualizado automáticamente.

## Trazabilidad

| Caso | Código de prueba | Código comprobado |
| --- | --- | --- |
| JU-01 | [AutonomoTest.java](src/test/java/AutonomoTest.java) | [Autonomo.java](src/main/java/Autonomo.java) |
| JU-02 | [TrabajadorCuentaAjenaTest.java](src/test/java/TrabajadorCuentaAjenaTest.java) | [TrabajadorCuentaAjena.java](src/main/java/TrabajadorCuentaAjena.java) |

## Límites

Se comprueban únicamente los dos escenarios de la tabla. Las pruebas no ejercitan todos los valores posibles, todos los métodos del modelo ni reglas de nóminas reales. No se ha medido un porcentaje de cobertura.

Como siguiente ampliación se podrían definir requisitos para horas negativas, horas cero y valores límite antes de escribir nuevos casos.

## English

Two original scenarios were run locally on October 9, 2026 using JDK 25, Java 17 compilation settings, Maven 3.9.9 and JUnit 5.13.4. Both passed, with no failures, errors or skipped tests.

- JU-01 checks `1800 + 35 × 60`, expecting `3900` within a tolerance of `0.1`.
- JU-02 checks `(2000.0f / 3.0f) × 1.18f`, expecting `786.67` within a tolerance of `0.1`.

The linked reports are snapshots of that run. Fresh reports are generated in `target/surefire-reports/`. These two cases do not establish complete coverage or validate a real payroll system.
