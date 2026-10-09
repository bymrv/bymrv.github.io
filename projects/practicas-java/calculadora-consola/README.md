# Calculadora de consola

Calculadora de menú para practicar métodos, condicionales, bucles y entrada por teclado. Incluye suma, resta, multiplicación, división, comparación de tres números y factorial.

## Ejecutar

Con Java 17 o posterior instalado, abre PowerShell en esta carpeta y ejecuta:

```powershell
.\run.ps1
```

También se puede compilar manualmente:

```powershell
javac -d build src/main/java/portfolio/nf1/Calculadora.java
java -cp build portfolio.nf1.Calculadora
```

## Ajustes de esta versión

- Los empates al buscar el mayor muestran correctamente el valor máximo.
- El factorial usa `long` y admite valores de 0 a 20, el rango que puede calcular sin desbordamiento.
- Se rechazan divisores cero, factoriales negativos y entradas no numéricas sin cerrar el programa.
- La calculadora conserva la selección por menú y las seis operaciones del ejercicio.
