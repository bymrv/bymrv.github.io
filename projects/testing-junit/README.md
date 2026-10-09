# Pruebas de remuneraciones con JUnit 5

Práctica de Entornos de Desarrollo: comprobar el cálculo de la remuneración de dos tipos de profesionales mediante pruebas unitarias en Java.

## Qué muestra

- Preparación de un objeto antes de cada prueba con `@BeforeEach`.
- Modificación de sus datos para reproducir un caso concreto.
- Comparación del resultado obtenido con el esperado mediante `assertEquals`.
- Uso de una tolerancia en las comparaciones de números decimales.
- Ejecución automática de las pruebas con Maven y JUnit 5.

## Ejecutar

Requiere un JDK 17 o posterior. Desde esta carpeta, con Maven instalado:

```sh
mvn test
```

En Windows también puedes usar:

```powershell
.\run-tests.ps1
```

El script busca un JDK instalado. Si Maven no está disponible, descarga la versión 3.9.9 desde Maven Central a `.tools` y verifica su SHA-512 antes de ejecutarla. La primera ejecución necesita Internet para descargar las dependencias. No modifica la configuración permanente del sistema.

Los informes se generan en `target/surefire-reports/`. Maven devuelve un código de salida distinto de cero si falla una prueba.

## Casos originales

| Caso | Preparación | Resultado esperado |
| --- | --- | --- |
| Autónomo | Base de 1800; precio por hora de 35; 60 horas | `1800 + 35 × 60 = 3900` |
| Trabajador por cuenta ajena | Base de `2000 / 3`; factor de la práctica `1.18` | Aproximadamente `786.67` |

Las dos pruebas conservan la tolerancia original de `0.1`. Las fórmulas son las del ejercicio y no representan un cálculo de nóminas reales.

## Procedencia y adaptación

Los casos de prueba proceden de la entrega de Manuel Rodríguez Vaca realizada en BlueJ durante el ciclo de DAW. Las clases del modelo parten del código facilitado para la actividad, cuyos archivos atribuyen su autoría a Juan Pedro Cerro Martínez. Esa atribución se conserva aquí.

Para esta versión se han organizado los archivos en `src/main/java` y `src/test/java`, añadido la configuración de Maven y un acceso de ejecución para PowerShell. Se han retirado los comentarios de plantilla; se mantienen las fórmulas, los datos de prueba, las aserciones y los nombres del ejercicio original.

## Alcance

Los dos casos comprueban cálculos concretos. No cubren entradas negativas, valores extremos ni reglas adicionales. Se conserva el uso original de `float`; una aplicación financiera necesitaría requisitos y tratamiento de importes más precisos. No se afirma cobertura completa.

Consulta [los casos y el registro de resultados](CASOS-DE-PRUEBA.md).

Referencias: [JUnit 5](https://docs.junit.org/5.13.4/user-guide/) y [Maven Surefire](https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit-platform.html).

## English

This coursework exercise uses JUnit 5 to check remuneration calculations for two types of professionals. Each test creates an object, changes its inputs and compares the calculated result with an expected value using a tolerance of `0.1`.

Run `mvn test` with JDK 17 or later and Maven installed. On Windows, `run-tests.ps1` can download and verify a project-local copy of Maven if needed. The first run needs an Internet connection. Reports are written to `target/surefire-reports/`.

The two test cases come from Manuel Rodríguez Vaca’s original BlueJ submission. The supplied model code is attributed to Juan Pedro Cerro Martínez. The portfolio adaptation adds the Maven project structure and execution script while preserving the original calculations, inputs and assertions. These are educational formulas, not a production payroll implementation. Only the two specified scenarios are covered.
