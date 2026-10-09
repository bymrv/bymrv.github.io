# Gestión de productos con JDBC y MySQL

Aplicación de consola basada en la práctica de tienda: permite crear, listar, modificar y borrar productos guardados en MySQL. Cada operación se ejecuta mediante JDBC y consultas preparadas.

## Requisitos

- JDK 17 o posterior.
- Maven 3.9 o posterior.
- MySQL 8.0.16 o posterior.

## Preparar la base de datos

Abre MySQL Workbench o una consola de MySQL y ejecuta primero `database/schema.sql`. Para cargar productos de muestra, ejecuta después `database/seed.sql`.

La configuración se lee desde variables de entorno:

| Variable | Valor por defecto |
| --- | --- |
| `DB_URL` | `jdbc:mysql://localhost:3306/tienda` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | vacío |

En PowerShell, si MySQL tiene contraseña:

```powershell
$env:DB_USER = 'root'
$env:DB_PASSWORD = 'tu contraseña'
```

## Ejecutar

Desde esta carpeta:

```powershell
.\run.ps1
```

El acceso directo compila con Maven y ejecuta la aplicación. El conector JDBC se descarga como dependencia de Maven; no se incluye el JAR antiguo que venía dentro del ZIP original. El proyecto sigue las coordenadas Maven indicadas en la [documentación de MySQL Connector/J](https://dev.mysql.com/doc/connector-j/en/connector-j-installing-maven.html).

## Qué se conserva y qué se ha corregido

- El menú original de altas, listado, modificación de un campo y borrado.
- Los campos `nombre`, `precio` y `cantidad`, con un identificador autogenerado.
- Consultas preparadas para insertar, modificar y borrar.
- Recursos JDBC cerrados automáticamente, validación de datos, configuración externa y precios con `BigDecimal`.

No se ha podido comprobar aquí la conexión de extremo a extremo porque este entorno no tiene un servidor MySQL ni Maven instalados. El código y el esquema quedan preparados para ejecutarse una vez estén disponibles esos requisitos.
