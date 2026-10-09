# Calculadora de escritorio con Swing

Calculadora de escritorio con teclado numérico, suma, resta, multiplicación, división, comprobación de paridad y registro de operaciones. Conserva las funciones principales de la práctica Swing original.

## Ejecutar

Con Java 17 o posterior y una sesión de escritorio disponible, desde PowerShell en esta carpeta:

```powershell
.\run.ps1
```

El historial se guarda en `data/historico.txt` dentro de esta carpeta. Ese fichero se crea al utilizar la calculadora y no se publica en GitHub.

## Cambios de esta versión

- La ventana usa gestores de distribución de Swing y se adapta al tamaño disponible.
- Se puede iniciar una operación desde la pantalla vacía y los errores de entrada o división por cero no cierran la ventana.
- El historial se carga al iniciar y se guarda en UTF-8.
