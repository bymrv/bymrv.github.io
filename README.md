# Manuel Rodríguez · Desarrollo de aplicaciones web

Proyectos de desarrollo web, programación en Java, bases de datos y testing realizados durante DAW y preparados para este portafolio.

**[Ver portafolio](https://bymrv.github.io/)** · **[LinkedIn](https://www.linkedin.com/in/manuel-rodriguez-vaca/)**

## Proyectos

| Proyecto | Tecnologías | Qué incluye |
| --- | --- | --- |
| [Barber Gold](barber-gold/) · [Demo](https://bymrv.github.io/barber-gold/) | HTML, CSS, JavaScript | Web de una barbería con servicios, galería e interacción mediante eventos |
| [La Despensa](projects/la-despensa/) | JavaScript, Node.js, SQLite, HTTP | Catálogo, cesta, pedidos y consultas sobre una base de datos relacional |
| [Programación en Java](projects/practicas-java/) | Java, Swing, JDBC, MySQL | Consola, orientación a objetos, interfaces gráficas y operaciones CRUD |
| [Pruebas con JUnit](projects/testing-junit/) | Java, JUnit 5, Maven | Dos casos de cálculo de remuneraciones, con datos y resultados documentados |

Barber Gold es el proyecto principal y conserva los archivos de la entrega original. Su formulario es una demostración: no envía reservas. La Despensa utiliza datos ficticios y no procesa pagos reales.

Cada proyecto incluye sus requisitos, instrucciones de ejecución y una descripción de su procedencia y de las adaptaciones realizadas.

## Ejecutar en local

```sh
git clone https://github.com/bymrv/bymrv.github.io.git
cd bymrv.github.io
```

Abre `index.html` en el navegador para ver el portafolio. No necesita instalar dependencias. La interfaz está disponible en español e inglés; la demo original de Barber Gold está en español.

La Despensa necesita Node.js 24.15 o posterior:

```sh
cd projects/la-despensa
node server.js
```

Después, abre `http://127.0.0.1:3000`. Para ejecutar los ejercicios de Java, consulta [su guía](projects/practicas-java/README.md).

## Pruebas

Los flujos de GitHub Actions ejecutan las pruebas cuando cambian los archivos de sus respectivos proyectos:

- **La Despensa:** nueve pruebas de consultas y de la API, incluyendo validación y gestión de existencias. [Casos documentados](projects/la-despensa/CASOS-DE-PRUEBA.md).
- **JUnit:** dos pruebas unitarias de remuneraciones sobre el modelo de la actividad. [Casos documentados](projects/testing-junit/CASOS-DE-PRUEBA.md).

Comandos para ejecutarlas desde la carpeta correspondiente:

```sh
node --test
```

```sh
mvn test
```

## Publicación

GitHub Pages publica la web desde la raíz de la rama `main`. El portafolio y Barber Gold se pueden visitar en el navegador. La Despensa y las aplicaciones Java requieren su entorno de ejecución local; su código e instrucciones están disponibles en este repositorio.

## Créditos

Las fuentes y licencias de los iconos se indican en [assets/icons](assets/icons/README.md). Los proyectos académicos conservan las atribuciones del material facilitado en clase.

## English

Web development, Java, database and testing projects from my DAW coursework and portfolio work.

**[View portfolio](https://bymrv.github.io/)** · **[LinkedIn](https://www.linkedin.com/in/manuel-rodriguez-vaca/)**

Barber Gold is the featured project, with its original Spanish-language demo. La Despensa demonstrates a catalogue, basket and order API backed by SQLite. The Java collection covers console applications, object-oriented programming, Swing interfaces and JDBC. The JUnit project contains two original test cases using the model supplied for the coursework.

Open `index.html` locally or visit the published portfolio. The portfolio supports Spanish and English. GitHub Pages hosts the static portfolio and Barber Gold; La Despensa requires Node.js 24.15 or later, and the Java projects require the tools listed in their setup guides.

GitHub Actions runs nine La Despensa tests and two JUnit tests when the corresponding project files change. Each project documents its scope, setup and adaptations. Booking and payment features are demonstrations and do not process real reservations or payments.
