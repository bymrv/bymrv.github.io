# Modelo de empresa

Ejercicio de orientación a objetos que representa una empresa con edificios, vehículos y productos. Se mantienen los tipos del original (fábrica, oficina, almacén; vehículos diésel, gasolina y eléctricos; sillas y puertas) y se muestra la información mediante una colección de objetos.

## Ejecutar

Con Java 17 o posterior, desde PowerShell en esta carpeta:

```powershell
.\run.ps1
```

## Qué se puede revisar

- Herencia en edificios, vehículos y productos.
- Una empresa guarda listas, no un número fijo de campos numerados.
- Cada edificio describe su función y calcula la superficie de sus seis caras.
- Los vehículos eléctricos conservan la potencia recibida al crearlos.
- El ejemplo construye una empresa de muestra y genera un informe.

La fórmula de pintura corrige la fórmula original, que multiplicaba ancho, alto y profundidad y devolvía volumen en vez de superficie. Para los edificios se interpretan las medidas originales (por ejemplo, 400 × 400 × 500) como centímetros y el precio se calcula por metro cuadrado. Calcula las seis caras de un prisma rectangular; no descuenta puertas ni ventanas.
